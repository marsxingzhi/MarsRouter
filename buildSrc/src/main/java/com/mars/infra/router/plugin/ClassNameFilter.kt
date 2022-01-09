package com.mars.infra.router.plugin

import java.util.*

/**
 * Created by JohnnySwordMan on 2022/1/8
 */
object ClassNameFilter {

    private val whiteList: MutableList<String> = LinkedList()

    init {
        whiteList.add("kotlin")
        whiteList.add("org")
        whiteList.add("androidx")
        whiteList.add("android")
    }

    fun filter(className: String): Boolean {
        whiteList.forEach {
            if (className.contains(it)) {
                return true
            }
        }
        return false
    }
}