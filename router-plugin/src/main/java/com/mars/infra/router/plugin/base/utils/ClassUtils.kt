package com.mars.infra.router.plugin.base.utils

import java.io.File
import java.io.FileOutputStream

/**
 * Created by JohnnySwordMan on 2022/1/14
 */
object ClassUtils {

    /**
     * entryName：com/mars/infra.router/TestLogin2.class
     * 返回值：com.mars.infra.router.TestLogin2
     */
    fun path2ClassName(entryName: String): String {
        return entryName.replace(".class", "")
            .replace('\\', '.')
            .replace('/', '.')
    }

    /**
     * 把bytes写到destFile文件中
     */
    fun saveFile(bytes: ByteArray?, destFile: File) {
        bytes?.apply {
            // 如果老文件存在，先删除
            if (destFile.exists()) {
                destFile.delete()
            }
            // 新建文件
            destFile.createNewFile()
            val stream = FileOutputStream(destFile)
            // use的用法：对于实现Closeable接口的对象，调用use函数，可以在结束的时候自动执行close，非常适合File操作
            stream.use {
                it.write(this)
            }
        }
    }

}