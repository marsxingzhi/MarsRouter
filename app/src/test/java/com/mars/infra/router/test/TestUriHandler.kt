package com.mars.infra.router.test

import android.util.Log
import com.mars.infra.router.RouterElement

/**
 * Created by JohnnySwordMan on 2022/1/5
 */
class TestUriHandler {

    companion object {
        val TAG: String = TestUriHandler::class.java.name
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