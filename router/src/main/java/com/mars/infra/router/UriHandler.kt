package com.mars.infra.router

import android.util.Log

/**
 * Created by JohnnySwordMan on 2022/1/5
 */
class UriHandler {

    companion object {
        val TAG: String = UriHandler::class.java.name
    }

    private val map = HashMap<RouterElement, String>()

    fun register(module: String?, path: String?, className: String) {
        val element = RouterElement(module, path)
        map[element] = className
        Log.e(TAG, "invoke register routerElement = $element, className = $className")
    }
}