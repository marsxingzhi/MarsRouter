package com.mars.infra.router.plugin.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

/**
 * Created by JohnnySwordMan on 2022/1/22
 */
class ServiceImplClassVisitor(
    private val api: Int,
    classVisitor: ClassVisitor,
    private val serviceImplSet: Set<String>?
) : ClassVisitor(api, classVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
         var mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name.equals("getService")) {
            mv = ServiceImplMethodVisitorV2(api, mv, serviceImplSet)
//            mv = TestServiceImplMethodVisitor(api, mv, serviceImplSet)
        }
        return mv
    }

}