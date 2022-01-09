package com.mars.router.plugin

import com.mars.router.plugin.visitor.RouterClassVisitor
import org.apache.commons.io.IOUtils
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

/**
 * Created by JohnnySwordMan on 2022/1/9
 */
object RegisterCodeGenerator {

    fun insertInitCode(routerSet: Set<String>, jarFile: File) {
        val oprJar = File(jarFile.parent, jarFile.name.toString() + ".opt")
        if (oprJar.exists()) {
            oprJar.delete()
        }
        val jarOutputStream = JarOutputStream(FileOutputStream(oprJar))
        // 操作Router.class所在的jar
        val file = JarFile(jarFile)
        val entries: Enumeration<JarEntry> = file.entries()
        while (entries.hasMoreElements()) {
            val jarEntry: JarEntry = entries.nextElement()
            val entryName: String = jarEntry.name
            val zipEntry = ZipEntry(entryName)
            val inputStream: InputStream = file.getInputStream(zipEntry)
            jarOutputStream.putNextEntry(zipEntry)
            if (entryName == RouterMappingCollector.ROUTER_PATH) {
                // 字节码插桩
                val bytes: ByteArray = createCodeWhenInit(routerSet, inputStream)
                jarOutputStream.write(bytes)
            } else {
                jarOutputStream.write(IOUtils.toByteArray(inputStream))
            }
            inputStream.close()
            jarOutputStream.closeEntry()
        }
        jarOutputStream.close()
        file.close()
        if (jarFile.exists()) {
            jarFile.delete()
        }
        oprJar.renameTo(jarFile)
    }

    private fun createCodeWhenInit(routerSet: Set<String>, inputStream: InputStream): ByteArray {
        val classReader = ClassReader(inputStream)
        val classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES)
        val routerClassVisitor = RouterClassVisitor(Opcodes.ASM7, classWriter, routerSet)
        classReader.accept(routerClassVisitor, ClassReader.EXPAND_FRAMES)
        return classWriter.toByteArray()
    }
}