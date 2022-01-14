package com.mars.infra.router.plugin.base

/**
 * Created by JohnnySwordMan on 2022/1/14
 */
interface DeleteCallback {

    fun delete(className: String, bytes: ByteArray)
}