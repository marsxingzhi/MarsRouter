package com.mars.infra.router.plugin.visitor.test

import com.mars.infra.router.plugin.base.ServiceImplData
import com.mars.infra.router.plugin.base.ServiceImplManager
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type

/**
 * Created by JohnnySwordMan on 2022/1/22
 */
class TestServiceImplMethodVisitor(
    api: Int,
    methodVisitor: MethodVisitor,
    private val serviceImplSet: Set<String>?
) : MethodVisitor(api, methodVisitor) {

    private var gotoLabel: Label? = null

    override fun visitCode() {
        super.visitCode()
        val serviceDataList = ServiceImplManager.getDataList()
        beforeInject()
        for (i in serviceDataList.indices) {
            val data: ServiceImplData = serviceDataList[i]
            inject(i == 0, i == serviceDataList.size - 1, data)
        }
        afterInject()
    }

    private fun inject(isStart: Boolean, isLast: Boolean, data: ServiceImplData) {
        mv.visitLdcInsn(Type.getType(java.lang.String.format("L%s;", data.interfaceClass)))
        mv.visitVarInsn(Opcodes.ALOAD, 0) //压栈
        val ifLabel: Label =
            if (isLast && gotoLabel != null) {
                gotoLabel!!
            } else {
                Label()
            }
        mv.visitJumpInsn(Opcodes.IF_ACMPNE, ifLabel) //不想等跳转
        val instanceLabel = Label()
        mv.visitLabel(instanceLabel)

        mv.visitTypeInsn(Opcodes.NEW, data.implementClass)
        mv.visitInsn(Opcodes.DUP) //出栈
        mv.visitMethodInsn(Opcodes.INVOKESPECIAL, data.implementClass, "<init>", "()V", false)
        mv.visitVarInsn(Opcodes.ASTORE, 1)
        if (isStart != isLast) {
            if (gotoLabel == null) {
                gotoLabel = Label()
            }
            mv.visitJumpInsn(Opcodes.GOTO, gotoLabel)
        }
        mv.visitLabel(ifLabel)
        if (isStart) {
            mv.visitFrame(Opcodes.F_APPEND, 1, arrayOf<Any>("java/lang/Object"), 0, null)
        } else {
            mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null)
        }
    }


    private fun beforeInject() {
        val startLabel = Label()
        mv.visitLabel(startLabel)
        mv.visitInsn(Opcodes.ACONST_NULL)
        mv.visitVarInsn(Opcodes.ASTORE, 1) //指定本地变量
        val ifStartLabel = Label()
        mv.visitLabel(ifStartLabel)
    }

    private fun afterInject() {
        mv.visitVarInsn(Opcodes.ALOAD, 1) //将变量推至栈顶
        mv.visitInsn(Opcodes.ARETURN) //return  object
    }
}