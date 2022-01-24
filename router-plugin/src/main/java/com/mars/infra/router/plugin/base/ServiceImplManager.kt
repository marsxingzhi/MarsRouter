package com.mars.infra.router.plugin.base

import java.util.*

/**
 * Created by JohnnySwordMan on 2022/1/24
 */
object ServiceImplManager {

    private val serviceImplDataList = Collections.synchronizedList(mutableListOf<ServiceImplData>())

    fun addData(data: ServiceImplData) {
        serviceImplDataList.add(data)
    }

    fun getDataList(): List<ServiceImplData> {
        return serviceImplDataList
    }
}