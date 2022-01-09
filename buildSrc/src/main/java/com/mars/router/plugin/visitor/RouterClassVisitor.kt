package com.mars.router.plugin.visitor

import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor

/**
 * Created by JohnnySwordMan on 2022/1/9
 */
class RouterClassVisitor(
    private val api: Int,
    classVisitor: ClassVisitor,
    private val routerSet: Set<String>?
) : ClassVisitor(api, classVisitor) {

    var className: String? = null

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        this.className = name
        println("RouterClassVisitor---visit---className = $className")
        super.visit(version, access, name, signature, superName, interfaces)
    }

    override fun visitMethod(
        access: Int,
        name: String?,
        descriptor: String?,
        signature: String?,
        exceptions: Array<out String>?
    ): MethodVisitor {
        var mv = super.visitMethod(access, name, descriptor, signature, exceptions)
        if (name.equals("loadRouterMap")) {
            mv = RouterMethodVisitor(api, mv, routerSet)
        }
        return mv
    }
}