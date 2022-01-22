//package com.mars.infra.router
//
///**
// * Created by JohnnySwordMan on 2022/1/22
// */
//internal object ServiceManager {
//
////    // test
////    private var test = false
////
////    // ASM
////    fun <T> getService(serviceClass: Class<T>): T? {
////        return null
////    }
//
//    fun <T> getService(serviceClass: Class<T>): T? {
//        return when (serviceClass) {
//            ILoginService::class.java -> {
//                val loginService = LoginServiceImpl()
//                loginService as T
//            }
//            IFakeService::class.java -> {
//                val fakeService = FakeServiceImpl()
//                fakeService as T
//            }
//            else -> {
//                null
//            }
//        }
//    }
//
//    fun testCollectServiceImplMap(): MutableMap<String, String> {
//        val map: MutableMap<String, String> = HashMap()
//        map["com.mars.infra.router.ILoginService"] = "com.mars.infra.router.LoginServiceImpl"
//        map["com.mars.infra.router.IFakeService"] = "com.mars.infra.router.FakeServiceImpl"
//        return map
//    }
//
//}