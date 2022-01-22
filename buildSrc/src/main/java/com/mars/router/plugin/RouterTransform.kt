package com.mars.router.plugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.TransformInvocation
import com.android.utils.FileUtils

/**
 * Created by JohnnySwordMan on 2022/1/9
 */
class RouterTransform : BaseTransform() {

    override fun getName(): String {
        return RouterTransform::class.java.simpleName
    }

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        transformInvocation ?: return
        // 如果不是增量编译，需要删除
        if (!transformInvocation.isIncremental) {
            transformInvocation.outputProvider.deleteAll()
        }
        // 消费型输入，可以从中获取jar包和class文件夹路径，需要输出给下一个任务
        val inputs = transformInvocation.inputs
        // OutputProvider管理消费型输出，如果消费型输入为空，你会发现OutputProvider为null
        val outputProvider = transformInvocation.outputProvider

        val collector = RouterMappingCollector()

        inputs?.forEach { input ->
            // 本地依赖或者远程依赖，以jar包的形式获取
            input.jarInputs?.forEach {
                val dest = outputProvider?.getContentLocation(
                    it.name,
                    it.contentTypes,
                    it.scopes,
                    Format.JAR
                )
                // 将修改后的字节码copy到dest，就可以实现编译期间干预字节码的目的了
//                collector.collectFromJarFile(it.file)
                FileUtils.copyFile(it.file, dest)

                // 该方法必须在copyFile之后，入参得是dest，
                // 如果放在前面调用，那么找到的Router.class所在的jar包是老的，还没有转换文件路径之前的，因此利用ASM修改，发现一直没有生效
                // 因为这个jar包不会打进apk中
                collector.collectFromJarFile(dest!!)
            }

            // apply该插件的模块的所有的类以directory的形式获取
            input.directoryInputs?.forEach {
                val dest = outputProvider?.getContentLocation(
                    it.name,
                    it.contentTypes,
                    it.scopes,
                    Format.DIRECTORY
                )
//                println("RouterTransform directoryInputs inputFile = ${it.file.absolutePath}")
//                println("RouterTransform directoryInputs outputFile = ${dest?.absolutePath}")

                // 这里需要写copyDirectory，使用copyFile会报异常，例如：RouterApp类不在base.apk中
                collector.collect(it.file)
                FileUtils.copyDirectory(it.file, dest)
            }
        }

        println("RouterTransform all mapping className = ${collector.routerMapping}")

        collector.destFile?.let {
            RegisterCodeGenerator.insertInitCode(collector.routerMapping, it)
        }
        collector.serviceImplFile?.let { file ->
//            RegisterCodeGenerator.insertServiceImplCode(collector.serviceMap, file)
        }
    }
}