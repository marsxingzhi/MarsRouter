package com.mars.infra.router.plugin

import com.android.build.api.transform.Format
import com.android.build.api.transform.QualifiedContent
import com.android.build.api.transform.Transform
import com.android.build.api.transform.TransformInvocation
import com.android.build.gradle.internal.pipeline.TransformManager
import com.android.utils.FileUtils
import com.mars.infra.router.plugin.visitor.RouterClassVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException

/**
 * Created by JohnnySwordMan on 2022/1/8
 */
class RouterTransform2 : Transform() {

    override fun getName(): String {
        return RouterTransform2::class.java.simpleName
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

    override fun transform(transformInvocation: TransformInvocation?) {
        super.transform(transformInvocation)
        transformInvocation ?: return
        // 如果不是增量编译，需要删除
        if (transformInvocation.isIncremental) {
            transformInvocation.outputProvider.deleteAll()
        }
        // 消费型输入，可以从中获取jar包和class文件夹路径，需要输出给下一个任务
        val inputs = transformInvocation.inputs
        // OutputProvider管理消费型输出，如果消费型输入为空，你会发现OutputProvider为null
        val outputProvider = transformInvocation.outputProvider
        inputs?.forEach { input ->
            input.jarInputs?.forEach {
                val dest = outputProvider?.getContentLocation(
                    it.file.absolutePath,
                    it.contentTypes,
                    it.scopes,
                    Format.JAR
                )
                // 将修改后的字节码copy到dest，就可以实现编译期间干预字节码的目的了
                try {
                    println("RouterTransform2---jarInputs inputFile = ${it.file.absolutePath}, destFile = " + dest?.absolutePath)
                    FileUtils.copyFile(it.file, dest)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
            input.directoryInputs?.forEach {
                val dest = outputProvider?.getContentLocation(
                    it.name,
                    it.contentTypes,
                    it.scopes,
                    Format.DIRECTORY
                )
                try {
                    println("RouterTransform2---directoryInputs inputFile = ${it.file.absolutePath}, destFile = " + dest?.absolutePath)
                    FileUtils.copyFile(it.file, dest)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
//                transformDir(it.file, dest)
            }
        }
    }

    private fun transformDir(input: File?, dest: File?) {
        if (input == null || dest == null) {
            return
        }
        if (dest.exists()) {
            FileUtils.delete(dest)
        }
        FileUtils.mkdirs(dest)

        val srcDirPath = input.absolutePath.apply {
            println("RouterTransform2---transformDir---srcDirPath = $this")
        }
        val destDirPath = dest.absolutePath.apply {
            println("RouterTransform2---transformDir---destDirPath = $this")
        }
        input.listFiles()?.forEach {
            val destFilePath = it.absolutePath.replace(srcDirPath, destDirPath)
            println("RouterTransform2---transformDir---destFilePath = $destFilePath")
            val destFile = File(destFilePath)
            if (it.isDirectory) {
                transformDir(it, destFile)
            } else if (it.isFile) {
//                weave(it.absolutePath, destFile.absolutePath)
            }
        }
    }

    private fun weave(inputPath: String, outputPath: String) {
        println(">>>>>> start RouterTransform weave <<<<<<")
        println("inputPath: $inputPath, outputPath: $outputPath")
        try {
            val fileInputStream = FileInputStream(inputPath)
            val classReader = ClassReader(fileInputStream)
            val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES)

            val routerClassVisitor = RouterClassVisitor(Opcodes.ASM7, classWriter, null)
            classReader.accept(routerClassVisitor, ClassReader.EXPAND_FRAMES)

            val bytes = classWriter.toByteArray()
            val fileOutputStream = FileOutputStream(File(outputPath))
            fileOutputStream.write(bytes)
            fileOutputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
        println(">>>>>> end RouterTransform weave <<<<<<")
    }
}