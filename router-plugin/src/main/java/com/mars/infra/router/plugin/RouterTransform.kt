package com.mars.infra.router.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import java.io.File

/**
 * Created by JohnnySwordMan on 2022/1/11
 */
class RouterTransform : Transform() {

    lateinit var mCollector: RouterCollector

    override fun getName(): String {
        return "${RouterTransform::class.java.simpleName}_v2"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    override fun isIncremental(): Boolean {
        return false
    }

    /**
     * 注意：
     * 1. apply该插件的模块的所有的类以directory的形式获取
     * 2. 本地依赖或者远程依赖，以jar包的形式获取
     */
    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        transformInvocation ?: return
        // 暂时不支持增量
        if (!transformInvocation.isIncremental) {
            transformInvocation.outputProvider.deleteAll()
        }

        mCollector = RouterCollector()

        val inputs = transformInvocation.inputs
        val outputProvider = transformInvocation.outputProvider
        inputs?.forEach {
            it.jarInputs?.forEach { jarInput ->
                val dest = outputProvider.getContentLocation(
                    jarInput.name,
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                foreachJar(jarInput, dest)
            }
            it.directoryInputs?.forEach { directoryInput ->
                val dest = outputProvider.getContentLocation(
                    directoryInput.name,
                    directoryInput.contentTypes,
                    directoryInput.scopes,
                    Format.DIRECTORY
                )
                foreachClass(directoryInput, dest)
            }
        }

        println("RouterTransform collect routerMap = ${mCollector.getRouterMap()}")
        println("RouterTransform collect path of Router.class = ${mCollector.getDestFile()?.absolutePath}")
        // 修改代码
        mCollector.getDestFile()?.let {
            RegisterCodeGenerator.insertInitCode(mCollector.getRouterMap(), it)
        }
    }

    private fun foreachJar(jarInput: JarInput, dest: File) {
        val inputFile = jarInput.file
        FileUtils.copyFile(inputFile, dest)

        // 由于需要找到Router.class所在jar包，因此需要在copy之后开始查找，因此是dest，不是inputFile
        mCollector.collectJarFile(dest)
    }


    private fun foreachClass(directoryInput: DirectoryInput, dest: File) {
        val inputFile = directoryInput.file
        // 注意：copyDirectory，不是copyFile
        FileUtils.copyDirectory(inputFile, dest)

        // 下面可以做一些class的遍历处理，可根据类名、包等信息，找到需要的类

        // ------
        /**
         * 我这里只是收集类名，因此先copy再收集，还是先收集再copy，区别不大。
         * 注意：如果是收集路径的话，需要在copy之后，例如：找到某个类所在的jar包，修改该类，那么就需要在copy之后，
         * 否则修改的是老的jar，这个老的jar包是不会打进apk中的
         */
        mCollector.collect(inputFile)
    }
}