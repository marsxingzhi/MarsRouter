package com.mars.infra.router.plugin

import com.mars.infra.router.plugin.visitor.ServiceImplCollectVisitor
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import org.objectweb.asm.Opcodes
import java.io.File
import java.io.FileInputStream
import java.util.jar.JarFile
import java.util.zip.ZipEntry

/**
 * Created by JohnnySwordMan on 2022/1/11
 */
class RouterCollector {

    companion object {
        private const val PACKAGE_NAME = "com/mars/infra/router/runtime"
        private const val PREFIX = "RouterMapping_"
        const val ROUTER_PATH = "com/mars/infra/router/runtime/Router.class"

        private const val SERVICE_IMPL_MAP_PREFIX = "ServiceImplMap_"
        const val SERVICE_MANAGER_PATH = "com/mars/infra/router/runtime/ServiceManager.class"
        const val DOWNGRADE_MANAGER_PATH = "com/mars/infra/router/runtime/DowngradeManager.class"
    }

    private val routerMap = mutableSetOf<String>()

    // Router.class所在jar包文件
    private var destFile: File? = null

    private val serviceImplSet = mutableSetOf<String>()

    // ServiceManager.class所在jar包文件
    private var serviceImplDestFile: File? = null

    // DowngradeManager.class所在jar包文件
    private var downgradeManagerDestFile: File? = null

    fun getRouterMap(): Set<String> {
        return routerMap
    }

    fun getDestFile(): File? {
        return destFile
    }

    fun getServiceImplSet(): Set<String> {
        return serviceImplSet
    }

    fun getServiceManagerDestFile(): File? {
        return serviceImplDestFile
    }

    fun getDowngradeManagerDestFile(): File? {
        return downgradeManagerDestFile
    }

    /**
     * collect 通过apt自动生成的RouterMapping_xxx的classname
     */
    fun collect(inputFile: File) {
        if (inputFile.isDirectory) {
            val files = inputFile.listFiles()
            if (files != null && files.isNotEmpty()) {
                for (child in files) {
                    collect(child)
                }
            }
        } else {
            if (inputFile.absolutePath.contains(PACKAGE_NAME)
                && inputFile.name.startsWith(PREFIX)
                && inputFile.name.endsWith(".class")
            ) {
                val className: String = inputFile.name.replace(".class", "")
                // 只收集类名
                routerMap.add(className)
            }

            if (inputFile.absolutePath.contains(PACKAGE_NAME)
                && inputFile.name.startsWith(SERVICE_IMPL_MAP_PREFIX)
                && inputFile.name.endsWith(".class")
            ) {
                val className: String = inputFile.name.replace(".class", "")
                serviceImplSet.add(className)
            }

            // 这里多做一次class的遍历，主要是为了收集ServiceImpl
            if (inputFile.absolutePath.contains("com/mars/infra/router")
                && inputFile.name.endsWith(".class")
            ) {
                // 过滤一下，只处理特定路径的、以及类文件
                val inputStream = FileInputStream(inputFile)
                val classReader = ClassReader(inputStream)
                val classWriter = ClassWriter(classReader, 0)
                val serviceImplCollect = ServiceImplCollectVisitor(Opcodes.ASM7, classWriter, "文件")
                classReader.accept(serviceImplCollect, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
                // 无须输出
//                val bytes = classWriter.toByteArray()
                inputStream.close()
            }
        }
    }

    /**
     * 1. collect 通过apt自动生成的RouterMapping_xxx的classname
     * 2. 找到Router.class所在的jar包
     */
    fun collectJarFile(file: File) {
        val classNames = JarUtils.scanJarFile(file)
        classNames.forEach { className ->

            if (className.contains(PACKAGE_NAME)
                && className.contains(PREFIX)
                && className.contains(".class")
            ) {

                val newClassName = className.replace(PACKAGE_NAME, "")
                    .replace("/", "")
                    .replace(".class", "")
                routerMap.add(newClassName)
            }

            if (className == ROUTER_PATH) {
                destFile = file
            }


            if (className.contains(PACKAGE_NAME)
                && className.contains(SERVICE_IMPL_MAP_PREFIX)
                && className.contains(".class")
            ) {

                val newClassName = className.replace(PACKAGE_NAME, "")
                    .replace("/", "")
                    .replace(".class", "")
                serviceImplSet.add(newClassName)
            }

            if (className == SERVICE_MANAGER_PATH) {
                serviceImplDestFile = file
            }
            if (className == DOWNGRADE_MANAGER_PATH) {
                downgradeManagerDestFile = file
            }
        }
        // 只遍历Jar包中的文件，不做任何处理
//        val jarOutputStream = JarOutputStream(FileOutputStream(file))
        val jarFile = JarFile(file)
        val entries = jarFile.entries()
        while (entries.hasMoreElements()) {
            val jarEntry = entries.nextElement()
            val entryName = jarEntry.name
            val zipEntry = ZipEntry(entryName)
            // 这里可以过滤一下，因为jar包中的类好多，有不少是android jar中的，这个是没有必要走ClassVisitor的
            if (entryName.contains("com/mars/infra/router") && entryName.endsWith(".class")) {
                val inputStream = jarFile.getInputStream(zipEntry)
                val classReader = ClassReader(inputStream)
                val classWriter = ClassWriter(classReader, 0)
                val serviceImplCollect =
                    ServiceImplCollectVisitor(Opcodes.ASM7, classWriter, "Jar包")
                classReader.accept(serviceImplCollect, ClassReader.SKIP_DEBUG or ClassReader.SKIP_FRAMES)
                // 不输出
//                val bytes = classWriter.toByteArray()
                inputStream.close()
            }
        }
        jarFile.close()
    }

}