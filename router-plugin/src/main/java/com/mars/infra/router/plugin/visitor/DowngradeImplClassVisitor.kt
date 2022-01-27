package com.mars.infra.router.plugin.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

/**
 * Created by JohnnySwordMan on 2022/1/27
 */
class DowngradeImplClassVisitor(
    api: Int,
    classVisitor: ClassVisitor
) : ClassVisitor(api, classVisitor) {

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        val mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name.equals("getDowngradeImpl")) {
            return DowngradeImplMethodVisitor(api, mv)
        }
        return mv
    }
}