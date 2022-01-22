package com.mars.infra.router.plugin

import java.io.File

/**
 * Created by JohnnySwordMan on 2022/1/11
 */
class RouterCollector {

    companion object {
        private const val PACKAGE_NAME = "com/mars/infra/router/runtime"
        private const val PREFIX = "RouterMapping_"
//        private const val SUFFIX = ".class"
        const val ROUTER_PATH = "com/mars/infra/router/runtime/Router.class"

        private const val SERVICE_IMPL_MAP_PREFIX = "ServiceImplMap_"
        const val SERVICE_MANAGER_PATH = "com/mars/infra/router/runtime/ServiceManager.class"
    }

    private val routerMap = mutableSetOf<String>()
    // Router.class所在jar包文件
    private var destFile: File? = null

    private val serviceImplSet = mutableSetOf<String>()
    // ServiceManager.class所在jar包文件
    private var serviceImplDestFile: File? = null

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
                && className.contains(".class")) {

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
                && className.contains(".class")) {

                val newClassName = className.replace(PACKAGE_NAME, "")
                    .replace("/", "")
                    .replace(".class", "")
                serviceImplSet.add(newClassName)
            }

            if (className == SERVICE_MANAGER_PATH) {
                serviceImplDestFile = file
            }
        }
    }

}