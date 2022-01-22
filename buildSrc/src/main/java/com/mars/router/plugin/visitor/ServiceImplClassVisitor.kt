package com.mars.router.plugin.visitor

import org.objectweb.asm.ClassVisitor

/**
 * Created by JohnnySwordMan on 2022/1/21
 */
class ServiceImplClassVisitor(
    private val api: Int,
    classVisitor: ClassVisitor,
    private val serviceImplSet: Set<String>?
) : ClassVisitor(api, classVisitor) {

}