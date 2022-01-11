package com.mars.infra.router.plugin

import java.io.File

/**
 * Created by JohnnySwordMan on 2022/1/11
 */
class RouterCollector {

    companion object {
        private const val PACKAGE_NAME = "com/mars/infra/router/runtime"
        private const val PREFIX = "RouterMapping_"
        private const val SUFFIX = ".class"
        const val ROUTER_PATH = "com/mars/infra/router/runtime/Router.class"
    }

    private val routerMap = mutableSetOf<String>()
    // Router.class所在jar包文件
    private var destFile: File? = null

    fun getRouterMap(): Set<String> {
        return routerMap
    }

    fun getDestFile(): File? {
        return destFile
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
                && inputFile.name.endsWith(SUFFIX)
            ) {
                val className: String = inputFile.name.replace(SUFFIX, "")
                // 只收集类名
                routerMap.add(className)
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
                && className.contains(SUFFIX)) {

                val newClassName = className.replace(PACKAGE_NAME, "")
                    .replace("/", "")
                    .replace(SUFFIX, "")
                routerMap.add(newClassName)
            }

            if (className == ROUTER_PATH) {
                destFile = file
            }
        }
    }

}