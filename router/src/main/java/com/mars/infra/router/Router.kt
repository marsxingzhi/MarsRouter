package com.mars.infra.router

import android.content.Context
import android.util.Log
import org.jetbrains.annotations.TestOnly

/**
 * Created by JohnnySwordMan on 2022/1/7
 */
object Router {

    private val uriHandler by lazy {
        UriHandler()
    }

    /**
     * 注册路由表
     * 通过接口，拿到所有实现类
     *
     * 获得当前app在手机中对应的apk文件，把它交给DexFile，这样可以获取到apk中所有的类
     */
    fun init(context: Context) {
        val classNames = ClassUtils.getFileNameByPackageName(context, "com.mars.infra.router")
        classNames.forEach { name ->
            val cls = Class.forName(name)

            if (checkClass(cls)) {
                val uriAnnotationInit = cls.newInstance() as IUriAnnotationInit
                uriAnnotationInit.init(uriHandler)
            }
        }
//        test()
    }


    /**
     * 检查cls是否是IUriAnnotationInit的实现类
     */
    private fun checkClass(cls: Class<*>): Boolean {
        if (cls.isInterface) {
            return false
        }
        // cls.isAssignableFrom(IUriAnnotationInit::class.java) 不生效
        return try {
            cls.genericInterfaces.isNotEmpty()
                    && (cls.genericInterfaces[0] as Class<*>).isAssignableFrom(
                IUriAnnotationInit::class.java
            )
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }

    private fun test() {
        val className = "com.mars.infra.router.UriAnnotationInit_996"
        try {
            val cls = Class.forName(className)
            val uriAnnotationInit = cls.newInstance() as IUriAnnotationInit
            uriAnnotationInit.init(uriHandler)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}