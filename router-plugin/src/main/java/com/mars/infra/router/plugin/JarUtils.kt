package com.mars.infra.router.plugin

import com.mars.infra.router.plugin.base.DeleteCallback
import org.apache.commons.io.IOUtils
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

    fun deleteJarScan(jarFile: File, mDeleteCallback: DeleteCallback?) {
        val file = JarFile(jarFile)
        file.use {
            val enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement()
                val entryName = jarEntry.name
                if (entryName.endsWith(".class")) {
                    val inputStream = file.getInputStream(jarEntry)
                    val bytes = IOUtils.toByteArray(inputStream)
                    mDeleteCallback?.delete(entryName, bytes)
                }
            }
        }
    }

    fun deleteJarScan(jarFile: File, removeClasses: List<String>, mDeleteCallback: DeleteCallback?) {
        val file = JarFile(jarFile)
        file.use {
            val enumeration = file.entries()
            while (enumeration.hasMoreElements()) {
                val jarEntry = enumeration.nextElement()
                val entryName = jarEntry.name

                if (entryName.endsWith(".class") && removeClasses.contains(entryName)) {
                    val inputStream = file.getInputStream(jarEntry)
                    val bytes = IOUtils.toByteArray(inputStream)
                    mDeleteCallback?.delete(entryName, bytes)
                }
            }
        }
    }
}