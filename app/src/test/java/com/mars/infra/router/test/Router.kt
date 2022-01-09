package com.mars.infra.router.test

/**
 * Created by JohnnySwordMan on 2022/1/9
 */
object Router {

    private var isInit = false

    private val uriHandler by lazy {
        TestUriHandler()
    }
    // /Users/geyan/projects/github/MarsRouter/app/src/test/java/com/mars/infra/router/com.mars.infra.router.test.Router.kt

    private fun loadRouterMap() {
//        UriAnnotationInit_996().init(uriHandler)
        TestUriAnnotationInit_5023().init(uriHandler)
        isInit = true
    }

}