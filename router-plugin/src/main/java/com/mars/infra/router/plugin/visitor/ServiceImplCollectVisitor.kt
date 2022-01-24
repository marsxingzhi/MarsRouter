package com.mars.infra.router.plugin.visitor

import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.ClassVisitor

/**
 * Created by JohnnySwordMan on 2022/1/24
 */
class ServiceImplCollectVisitor(
    api: Int,
    classVisitor: ClassVisitor,
    private val tag: String
) : ClassVisitor(api, classVisitor) {

    companion object {
        const val ANNOTATION_SERVICE_IMPL = "Lcom/mars/infra/router/api/ServiceImpl;"
    }


    private var className: String? = null

    override fun visit(
        version: Int,
        access: Int,
        name: String?,
        signature: String?,
        superName: String?,
        interfaces: Array<out String>?
    ) {
        /**
         * 打印：
         * ServiceImplCollectVisitor tag = Jar包, name = com/mars/infra/router/runtime/Router
         * ServiceImplCollectVisitor tag = Jar包, name = androidx/constraintlayout/core/motion/utils/Rect
         * ServiceImplCollectVisitor tag = 文件, name = com/mars/infra/router/RouterApp
         */
        super.visit(version, access, name, signature, superName, interfaces)
        println("ServiceImplCollectVisitor tag = $tag, name = $name")
        className = name
    }

    override fun visitAnnotation(descriptor: String?, visible: Boolean): AnnotationVisitor {
        val visitor = super.visitAnnotation(descriptor, visible)
        if (descriptor == ANNOTATION_SERVICE_IMPL) {
            return ServiceImplAnnotationVisitor(className, api, visitor)
        }
        return visitor
    }
}