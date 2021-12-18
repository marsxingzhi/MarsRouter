package com.mars.infra.router.api

import java.lang.annotation.ElementType

/**
 * Created by JohnnySwordMan on 2022/1/5
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.RUNTIME)
annotation class RouterUri(
    val module: String,
    val path: String
)
