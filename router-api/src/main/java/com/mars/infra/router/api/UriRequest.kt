package com.mars.infra.router.api

/**
 * Created by JohnnySwordMan on 2022/1/12
 */
class UriRequest {
    var uri: String? = null

    var param1: String? = null

    var param2: String? = null


    object ResultCode {
        const val SUCCESS = 0
        const val ERROR = -1
    }

}



interface UriCallback {

    fun onNext()

    fun onComplete(resultCode: Int)
}
