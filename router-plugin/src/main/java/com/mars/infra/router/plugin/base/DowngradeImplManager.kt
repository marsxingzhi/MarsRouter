package com.mars.infra.router.plugin.base

import java.util.*

/**
 * Created by JohnnySwordMan on 2022/1/27
 */
object DowngradeImplManager {

    private val downgradeImplDataList = Collections.synchronizedList(mutableListOf<DowngradeImplData>())

    fun addData(data: DowngradeImplData) {
        downgradeImplDataList.add(data)
    }

    fun getDataList(): List<DowngradeImplData> {
        return downgradeImplDataList
    }
}