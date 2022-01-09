package com.mars.infra.router.runtime

/**
 * Created by JohnnySwordMan on 2022/1/5
 */
interface IUriAnnotationInit {
    
    fun init(handler: UriHandler)
}