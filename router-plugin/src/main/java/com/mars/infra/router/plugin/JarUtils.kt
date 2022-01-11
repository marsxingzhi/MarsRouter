package com.mars.infra.router.plugin

import java.io.File
import java.util.*
import java.util.jar.JarEntry
import java.util.jar.JarFile
import kotlin.collections.HashSet

/**
 * Created by JohnnySwordMan on 2022/1/12
 */
object JarUtils {

    fun scanJarFile(jarFile: File): Set<String> {
        val hashSet = HashSet<String>()
        val file = JarFile(jarFile)
        val enumeration: Enumeration<JarEntry> = file.entries()
        while (enumeration.hasMoreElements()) {
            val jarEntry: JarEntry = enumeration.nextElement()
            val entryName: String = jarEntry.name

            // entryName = androidx/arch/core/R.class
            // entryName = com/mars/infra/router/Router.class
            if (entryName.endsWith(".class")) {
                hashSet.add(entryName)
            }
        }
        file.close()
        return hashSet
    }
}