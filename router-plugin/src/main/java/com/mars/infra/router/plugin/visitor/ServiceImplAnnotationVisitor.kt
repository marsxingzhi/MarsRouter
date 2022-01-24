package com.mars.infra.router.plugin.visitor

import com.mars.infra.router.plugin.base.ServiceImplData
import com.mars.infra.router.plugin.base.ServiceImplManager
import org.objectweb.asm.AnnotationVisitor
import org.objectweb.asm.Type

/**
 * Created by JohnnySwordMan on 2022/1/24
 */
class ServiceImplAnnotationVisitor(
    private val className: String?,
    api: Int,
    annotationVisitor: AnnotationVisitor
) : AnnotationVisitor(api, annotationVisitor) {

    private val mServiceInterfaceList = arrayListOf<Type>()

    override fun visitArray(name: String?): AnnotationVisitor {
        val visitArray = super.visitArray(name)
        if (name == "service") {
            return object : AnnotationVisitor(api, visitArray) {
                override fun visit(name: String?, value: Any?) {
                    if (value is Type) {
                        mServiceInterfaceList.add(value)
                    }
                    super.visit(name, value)
                }
            }
        }
        return visitArray
    }

    override fun visitEnd() {
        super.visitEnd()
        // interface name = com.mars.infra.router.ILoginService,
        // serviceImpl name = com/mars/infra/router/LoginServiceImpl
        mServiceInterfaceList.forEach {type ->
            val serviceImplData = ServiceImplData(type.className, className!!, false)
            println("ServiceImplAnnotationVisitor---serviceImplData = $serviceImplData")
            ServiceImplManager.addData(serviceImplData)
        }
    }
}