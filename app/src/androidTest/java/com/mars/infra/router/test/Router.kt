package com.mars.infra.router.test

import java.util.*

/**
 * Created by JohnnySwordMan on 2022/1/9
 */
object Router {

    private var isInit = false
    private val routerMap = HashMap<Any?, Any?>()

    private fun loadRouterMap() {
//        routerMap.putAll(RouterMapping_1024.get())
        isInit = true
    }
}