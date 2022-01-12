package com.mars.infra.router.runtime.interceptor

import com.mars.infra.router.api.IUriInterceptor
import com.mars.infra.router.api.UriCallback
import com.mars.infra.router.api.UriRequest

/**
 * Created by JohnnySwordMan on 2022/1/12
 */
class DefaultInterceptor: IUriInterceptor {

    override fun intercept(request: UriRequest, callback: UriCallback) {
        // 默认做一些处理
        if (request.uri.isNullOrEmpty()) {
            callback.onComplete(-1)
        } else {
            callback.onNext()
        }
    }
}