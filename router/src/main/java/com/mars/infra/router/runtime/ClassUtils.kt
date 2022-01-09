package com.mars.infra.router.runtime

import android.content.Context
import android.os.Build
import dalvik.system.DexFile
import kotlin.collections.HashSet

/**
 * Created by JohnnySwordMan on 2022/1/7
 */
object ClassUtils {

    fun getFileNameByPackageName(context: Context, packageName: String): HashSet<String> {
        val sourcePaths = getSourcePaths(context)
        val classNames = HashSet<String>()
        sourcePaths.forEach { path ->
            var dexFile: DexFile? = null
            try {
                // 加载apk中的dex，并遍历所有的packageName中的类
                dexFile = DexFile(path)
                val entries = dexFile.entries()
                while (entries.hasMoreElements()) {
                    val className = entries.nextElement()
                    if (className.startsWith(packageName)) {
                        classNames.add(className)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                dexFile?.close()
            }
        }
        return classNames
    }

    /**
     * 获取程序所有的apk
     */
    private fun getSourcePaths(context: Context): List<String> {
        val applicationInfo = context.packageManager.getApplicationInfo(context.packageName, 0)
        val sourcePaths = mutableListOf<String>()
        // 当前应用的apk文件
        sourcePaths.add(applicationInfo.sourceDir)
        // instant run，instant run会产生很多的split apk
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            if (applicationInfo.splitSourceDirs != null) {
                sourcePaths.addAll(applicationInfo.splitSourceDirs.toList())
            }
        }
        return sourcePaths
    }
}