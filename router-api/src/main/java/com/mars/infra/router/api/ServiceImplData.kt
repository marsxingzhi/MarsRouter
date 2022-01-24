package com.mars.infra.router.api

/**
 * Created by JohnnySwordMan on 2022/1/17
 */
data class ServiceImplData(
    val interfaceClass: String,
    val implementClass: String,
    val singleton: Boolean
)