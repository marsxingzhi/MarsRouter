package com.mars.router.plugin

import java.io.File
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile


/**
 * Created by JohnnySwordMan on 2022/1/9
 *
 * 收集com.mars.infra.router.runtime.RouterMapping_1024
 */
class RouterMappingCollector {

    companion object {
        private const val PACKAGE_NAME = "com/mars/infra/router/runtime"
        private const val PREFIX = "RouterMapping_"
        private const val SUFFIX = ".class"
        const val ROUTER_PATH = "com/mars/infra/router/runtime/Router.class"
    }

    val routerMapping = mutableSetOf<String>()
    var destFile: File? = null

    fun collectFromJarFile(jarFile: File) {
        val entries: Enumeration<JarEntry> = JarFile(jarFile).entries()
        while (entries.hasMoreElements()) {
            val jarEntry: JarEntry = entries.nextElement()
            val entryName: String = jarEntry.name
            // entryName = androidx/arch/core/R.class
            // RouterMappingCollector collectFromJarFile entryName = com/mars/infra/router/Router.class
            if (entryName.contains(PACKAGE_NAME)
                && entryName.contains(PREFIX)
                && entryName.contains(SUFFIX)
            ) {
                val className: String = entryName.replace(PACKAGE_NAME, "")
                    .replace("/", "")
                    .replace(SUFFIX, "")
                println("RouterMappingCollector collectFromJarFile className = $className")
                routerMapping.add(className)
            }

            if (entryName == ROUTER_PATH) {
                println("RouterMappingCollector collectFromJarFile 找到Router文件了，entryName = $entryName")
                println("RouterMappingCollector collectFromJarFile 找到Router文件了，file = " + jarFile.absolutePath)
                // 注意：需要将该方法放在copyFile之后，传入destFile。否则会出错，因为jar文件已经转移了
                destFile = jarFile
            }
        }
    }

    fun collect(file: File?) {
        if (file == null || !file.exists()) {
            return
        }
        if (file.isDirectory) {
            val files = file.listFiles()
            if (files != null && files.isNotEmpty()) {
                for (child in files) {
                    collect(child)
                }
            }
        } else {
            if (file.absolutePath.contains(PACKAGE_NAME)
                && file.name.startsWith(PREFIX)
                && file.name.endsWith(SUFFIX)
            ) {
                val className: String = file.name.replace(SUFFIX, "")
                println("RouterMappingCollector collect file = " + file.absolutePath)
                routerMapping.add(className)
            }
        }
    }
}