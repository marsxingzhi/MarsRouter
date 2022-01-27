package com.mars.infra.router.api

import kotlin.reflect.KClass

/**
 * Created by JohnnySwordMan on 2022/1/27
 */
@Target(AnnotationTarget.CLASS)
@Retention(AnnotationRetention.BINARY)
annotation class DowngradeImpl(
    val service: Array<KClass<*>>,
    val singleton: Boolean = false,
    val isForceDowngrade: Boolean = false
)