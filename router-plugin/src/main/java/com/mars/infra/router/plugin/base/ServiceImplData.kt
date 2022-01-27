package com.mars.infra.router.plugin.base

/**
 * Created by JohnnySwordMan on 2022/1/24
 */
data class ServiceImplData(
    val interfaceClass: String,  // com.mars.infra.router.ILoginService
    val implementClass: String,  // com/mars/infra/router/LoginServiceImpl
    val singleton: Boolean
)

data class DowngradeImplData(
    val interfaceClass: String,  // com.mars.infra.router.ILoginService
    val implementClass: String,  // com/mars/infra/router/LoginServiceImpl
    val singleton: Boolean,
    val isForceDowngrade: Boolean
)