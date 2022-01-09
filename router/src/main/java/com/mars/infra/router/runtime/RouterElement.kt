package com.mars.infra.router.runtime

/**
 * Created by JohnnySwordMan on 2022/1/7
 */
data class RouterElement(
    val module: String?,
    val path: String?
) {
    override fun toString(): String {
        return "$module-$path"
    }
}
