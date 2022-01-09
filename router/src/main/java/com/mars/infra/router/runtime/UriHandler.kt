package com.mars.infra.router.runtime

import android.util.Log

/**
 * Created by JohnnySwordMan on 2022/1/5
 */
class UriHandler {

    companion object {
        val TAG: String = UriHandler::class.java.name
    }

//    private val map = HashMap<RouterElement, String>()
    private val map = HashMap<String?, String>()

    fun register(module: String?, path: String?, className: String) {
        val element = RouterElement(module, path)
        map[path] = className
        Log.e(TAG, "invoke register routerElement = $element, className = $className")
    }

    fun getValue(path: String?): String? {
        return map[path]
    }
}