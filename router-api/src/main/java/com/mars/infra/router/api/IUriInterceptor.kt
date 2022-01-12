package com.mars.infra.router.api

/**
 * Created by JohnnySwordMan on 2022/1/12
 */
interface IUriInterceptor {

    fun intercept(request: UriRequest, callback: UriCallback)
}