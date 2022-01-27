package com.mars.infra.router.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import com.google.common.io.Files
import com.mars.infra.router.plugin.base.DeleteCallback
import com.mars.infra.router.plugin.base.SetDiff
import com.mars.infra.router.plugin.base.utils.ClassUtils
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import java.io.File
import java.io.FileInputStream

/**
 * Created by JohnnySwordMan on 2022/1/11
 */
class RouterTransform : Transform() {

    lateinit var mCollector: RouterCollector

    private var mDeleteCallback: DeleteCallback? = null
    private var outputProvider: TransformOutputProvider? = null
    private var mIsIncremental = false


    override fun getName(): String {
        return "${RouterTransform::class.java.simpleName}_0.2.12"
    }

    override fun getInputTypes(): MutableSet<QualifiedContent.ContentType> {
        return TransformManager.CONTENT_CLASS
    }

    override fun getScopes(): MutableSet<in QualifiedContent.Scope> {
        return TransformManager.SCOPE_FULL_PROJECT
    }

    // 支持增量编译
    override fun isIncremental(): Boolean {
        return true
    }

    /**
     * 注意：
     * 1. apply该插件的模块的所有的类以directory的形式获取
     * 2. 本地依赖或者远程依赖，以jar包的形式获取
     */
    override fun transform(transformInvocation: TransformInvocation) {
        super.transform(transformInvocation)
        // 如果不支持增量编译，则删除目录
        mIsIncremental = transformInvocation.isIncremental
        val inputs = transformInvocation.inputs
        outputProvider = transformInvocation.outputProvider

        println("RouterTransform---mIsIncremental---😋 = $mIsIncremental")
        println("RouterTransform---isIncremental---😋 = $isIncremental")

        // isIncremental
        if (!mIsIncremental) {
            try {
                transformInvocation.outputProvider.deleteAll()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        mCollector = RouterCollector()

        inputs?.forEach {
            it.jarInputs?.forEach { jarInput ->
                val status = jarInput.status
                var destName = jarInput.file.name
                // jar包重命名，因为可能存在同名文件，同名文件会覆盖
                val hash = DigestUtils.md5Hex(jarInput.file.absolutePath).substring(0, 8)
                if (destName.endsWith(".jar")) {
                    destName = destName.substring(0, destName.length - 4)
                }
                val dest = outputProvider!!.getContentLocation(
                    "${destName}_${hash}",
                    jarInput.contentTypes,
                    jarInput.scopes,
                    Format.JAR
                )
                if (mIsIncremental) {
                    when (status) {
                        Status.ADDED -> {
                            foreachJar(jarInput, dest)
                        }
                        Status.CHANGED -> {
                            // 1.扫描出改变的类；2.同ADD操作
                            diffJar(jarInput, dest)
                        }
                        Status.REMOVED -> {
                            deleteScan(dest)
                            if (dest.exists()) {
                                FileUtils.forceDelete(dest)
                            }
                        }
                        else -> {

                        }
                    }
                } else {
                    foreachJar(jarInput, dest)
                }
            }
            it.directoryInputs?.forEach { directoryInput ->
                foreachClass(directoryInput)
            }
        }

        println("RouterTransform collect routerMap = ${mCollector.getRouterMap()}")
        println("RouterTransform collect path of Router.class = ${mCollector.getDestFile()?.absolutePath}")

        println("RouterTransform collect serviceImplSet = ${mCollector.getServiceImplSet()}")
        println("RouterTransform collect path of ServiceManager.class = ${mCollector.getServiceManagerDestFile()?.absolutePath}")
        println("RouterTransform collect path of DowngradeManager.class = ${mCollector.getDowngradeManagerDestFile()?.absolutePath}")

        // 修改代码
//        mCollector.getDestFile()?.let {
//            RegisterCodeGenerator.insertInitCode(mCollector.getRouterMap(), it)
//        }
        // 其实Router.class、ServiceManager.class、DowngradeManager.class是在一个jar包的
        val routerDestFile = mCollector.getDestFile()
        val serviceManagerFile = mCollector.getServiceManagerDestFile()
        val downgradeManagerFile = mCollector.getDowngradeManagerDestFile()

        routerDestFile?.let {
            RegisterCodeGenerator.insertInitCode(mCollector.getRouterMap(), mCollector.getServiceImplSet(), it)
        }

//        if (serviceManagerFile != null
//            && downgradeManagerFile != null
//            && serviceManagerFile.absolutePath.equals(downgradeManagerFile.absolutePath)) {
//            RegisterCodeGenerator.insertServiceImplAndDowngradeImplCode(mCollector.getServiceImplSet(), serviceManagerFile)
//        } else {
//            serviceManagerFile?.let { file ->
//                RegisterCodeGenerator.insertServiceImplMap(mCollector.getServiceImplSet(), file)
//            }
//            downgradeManagerFile?.let { file ->
//                RegisterCodeGenerator.insertDowngradeImplMap(file)
//            }
//        }
//        if (mCollector.getServiceManagerDestFile()?.absolutePath.equals(mCollector.getDowngradeManagerDestFile()?.absolutePath)) {
//            RegisterCodeGenerator.insertServiceImplAndDowngradeImplCode(
//                mCollector.getServiceImplSet(),
//                mCollector.getServiceManagerDestFile()
//            )
//        } else {
//            mCollector.getServiceManagerDestFile()?.let { file ->
//                RegisterCodeGenerator.insertServiceImplMap(mCollector.getServiceImplSet(), file)
//            }
//            mCollector.getDowngradeManagerDestFile()?.let { file ->
//                RegisterCodeGenerator.insertDowngradeImplMap(file)
//            }
//        }
    }

    private fun foreachClass(directoryInput: DirectoryInput) {
        val dest = outputProvider!!.getContentLocation(
            directoryInput.name,
            directoryInput.contentTypes,
            directoryInput.scopes,
            Format.DIRECTORY
        )
        // 获取修改的文件
        val map = directoryInput.changedFiles
        val dir = directoryInput.file
        if (mIsIncremental) {
            map.forEach { (file, status) ->
                /**
                 * file: 可以理解成修改的class文件
                 * dir: 输入目录
                 * dest: 输出目录
                 *
                 * 举例：
                 * file：/Users/geyan/projects/github/MarsRouter/app/build/tmp/kotlin-classes/debug/com/mars/infra/router/TestLogin2.class
                 * dir：/Users/geyan/projects/github/MarsRouter/app/build/tmp/kotlin-classes/debug
                 * dest：/Users/geyan/projects/github/MarsRouter/app/build/intermediates/transforms/RouterTransform_v2/debug/55
                 */
                val destFilePath =
                    file.absolutePath.replace(dir.absolutePath, dest.absolutePath)
                val destFile = File(destFilePath)
                when (status) {
                    Status.REMOVED -> {
                        deleteDirectory(destFile, dest)
                    }
                    Status.ADDED, Status.CHANGED -> {
                        // 全部拷贝
                        try {
                            FileUtils.touch(destFile)
                        } catch (ignored: Exception) {
                            Files.createParentDirs(destFile)
                        }
                        modifySingleFile(file, dir, destFile)
                    }
                    else -> {

                    }
                }
            }
        } else {
            changeFile(dir, dest)
        }
    }


    private fun diffJar(jarInput: JarInput, dest: File) {
        // 注意：此时dest是老的文件，jarInput是新的，还未输入
        val oldJarFileName = JarUtils.scanJarFile(dest)
        val newJarFileName = JarUtils.scanJarFile(jarInput.file)

        // diff方案，这里只能知道类级别的，哪些类不删除了，哪些类新增的，但是类的内容改动不清楚，因为只是比较了className
        val diff = SetDiff(oldJarFileName, newJarFileName)

        val removeList = diff.getRemovedList()
        if (removeList.isNotEmpty()) {
            JarUtils.deleteJarScan(dest, removeList, mDeleteCallback)
        }
        // CHANGED状态，最终也是同ADD一样，执行copy操作
        foreachJar(jarInput, dest)
    }

    /**
     * 搜寻被删除的文件
     */
    private fun deleteScan(dest: File) {
        JarUtils.deleteJarScan(dest, mDeleteCallback)
    }

    /**
     * file：新增或修改的文件
     * dir：输入目录
     * destFile：输出文件
     */
    private fun modifySingleFile(file: File, dir: File, destFile: File) {
        try {
            val classPath = file.absolutePath.replace(dir.absolutePath + File.separator, "")
            val className = ClassUtils.path2ClassName(classPath)
            if (classPath.endsWith(".class")) {
                val bytes = IOUtils.toByteArray(FileInputStream(file))
                ClassUtils.saveFile(bytes, destFile)
            } else {
                if (!file.isDirectory) {
                    FileUtils.copyFile(file, destFile)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 1. 搜寻被删除的文件
     * 2. 删除
     */
    private fun deleteDirectory(destFile: File, dest: File) {
        try {
            if (destFile.isDirectory) {
                destFile.walkTopDown().forEach { classFile ->
                    deleteSingleScan(classFile, dest)
                }
//                for (classFile in com.android.utils.FileUtils.getAllFiles(destFile)) {
//                    deleteSingleScan(classFile, dest)
//                }
            } else {
                deleteSingleScan(destFile, dest)
            }
        } catch (ignored: Exception) {

        }
        try {
            // 增量编译下，文件一直打不进apk，罪魁祸首！！！
//            if (dest.exists()) {
//                FileUtils.forceDelete(dest)
//            }
            if (destFile.exists()) {
                FileUtils.forceDelete(destFile)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun deleteSingleScan(classFile: File, dest: File) {
        try {
            if (classFile.absolutePath.endsWith(".class")) {
                // classPath = com/mars/infra.router/TestLogin2.class
                val classPath =
                    classFile.absolutePath.replace(dest.absolutePath + File.separator, "")
                val className = ClassUtils.path2ClassName(classPath)
                val deletedFileBytes = IOUtils.toByteArray(FileInputStream(classFile))
                mDeleteCallback?.delete(className, deletedFileBytes)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    private fun foreachJar(jarInput: JarInput, dest: File) {
        val inputFile = jarInput.file
        FileUtils.copyFile(inputFile, dest)

        // 由于需要找到Router.class所在jar包，因此需要在copy之后开始查找，因此是dest，不是inputFile
        mCollector.collectJarFile(dest)
    }


    private fun changeFile(dir: File, dest: File) {
        // 注意：copyDirectory，不是copyFile
        if (dir.isDirectory) {
            FileUtils.copyDirectory(dir, dest)
        }


        // 下面可以做一些class的遍历处理，可根据类名、包等信息，找到需要的类

        // ------
        /**
         * 我这里只是收集类名，因此先copy再收集，还是先收集再copy，区别不大。
         * 注意：如果是收集路径的话，需要在copy之后，例如：找到某个类所在的jar包，修改该类，那么就需要在copy之后，
         * 否则修改的是老的jar，这个老的jar包是不会打进apk中的
         */
        mCollector.collect(dir)
    }
}