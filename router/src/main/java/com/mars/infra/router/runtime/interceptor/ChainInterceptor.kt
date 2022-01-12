package com.mars.infra.router.runtime.interceptor

import com.mars.infra.router.api.IUriInterceptor
import com.mars.infra.router.api.UriCallback
import com.mars.infra.router.api.UriRequest

/**
 * Created by JohnnySwordMan on 2022/1/12
 */
class ChainInterceptor : IUriInterceptor {

    private val interceptors = mutableListOf<IUriInterceptor>()

    fun addInterceptor(interceptor: IUriInterceptor) {
        interceptors.add(interceptor)
    }

    fun addInterceptor(interceptorList: List<IUriInterceptor>) {
        interceptors.addAll(interceptorList)
    }

    override fun intercept(request: UriRequest, callback: UriCallback) {
        next(interceptors.iterator(), request, callback)
    }

    private fun next(
        iterator: MutableIterator<IUriInterceptor>,
        request: UriRequest,
        callback: UriCallback
    ) {
        if (iterator.hasNext()) {
            val uriInterceptor = iterator.next()
            uriInterceptor.intercept(request, object : UriCallback {
                override fun onNext() {
//                    callback.onNext()
                    // 下一个拦截器
                    next(iterator, request, callback)
                }

                override fun onComplete(resultCode: Int) {
                    callback.onComplete(resultCode)
                }
            })
        } else {
            // 所有拦截器都走完了，走真正的处理逻辑
            callback.onNext()
        }
    }
}