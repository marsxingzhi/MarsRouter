package com.mars.infra.router.plugin.visitor.test

import com.mars.infra.router.plugin.base.ServiceImplData

/**
 * Created by JohnnySwordMan on 2022/1/22
 */
object TestUtils {

    fun getServiceData(): List<ServiceImplData> {
        val list = mutableListOf<ServiceImplData>()
//        list.add(ServiceImplData().also {
//            it.mInterface = "com.mars.infra.router.IFakeService"
//            it.mClassName = "com/mars/infra/router/FakeServiceImpl"
//        })
//        list.add(ServiceImplData().also {
//            it.mInterface = "com.mars.infra.router.ILoginService"
//            it.mClassName = "com/mars/infra/router/LoginServiceImpl"
//        })
//        list.add(ServiceImplData().also {
//            it.mInterface = "com.mars.infra.router.IAccountService"
//            it.mClassName = "com/mars/infra/router/AccountServiceImpl"
//        })
        return list
    }
}