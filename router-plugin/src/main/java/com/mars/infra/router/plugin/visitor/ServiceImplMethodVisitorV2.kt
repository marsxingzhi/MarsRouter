package com.mars.infra.router.plugin.visitor

import com.mars.infra.router.plugin.visitor.test.ServiceImplData
import com.mars.infra.router.plugin.visitor.test.TestUtils
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes


class ServiceImplMethodVisitorV2(
    api: Int,
    methodVisitor: MethodVisitor,
    private val serviceImplSet: Set<String>?
) : MethodVisitor(api, methodVisitor) {

    private fun inject(isStart: Boolean, isLast: Boolean, data: ServiceImplData) {
        val label = Label()
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/Class",
            "getName",
            "()Ljava/lang/String;",
            false
        )
        mv.visitLdcInsn(data.mInterface)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/String",
            "equals",
            "(Ljava/lang/Object;)Z",
            false
        )
        mv.visitJumpInsn(Opcodes.IFEQ, label)
        mv.visitTypeInsn(Opcodes.NEW, data.mClassName)
        mv.visitInsn(Opcodes.DUP)
        mv.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            data.mClassName,
            "<init>",
            "()V",
            false
        )
        mv.visitVarInsn(Opcodes.ASTORE, 1)
        mv.visitVarInsn(Opcodes.ALOAD, 1)
        mv.visitInsn(Opcodes.ARETURN)
        mv.visitLabel(label)
    }

    override fun visitInsn(opcode: Int) {
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {

            mv.visitCode()

            val serviceDataList = TestUtils.getServiceData()
            for (i in serviceDataList.indices) {
                val data: ServiceImplData = serviceDataList[i]
                inject(i == 0, i == serviceDataList.size - 1, data)
            }

            mv.visitInsn(Opcodes.ACONST_NULL)
            mv.visitInsn(Opcodes.ARETURN)
            mv.visitMaxs(2, 2)

            mv.visitEnd()
        }
        super.visitInsn(opcode)
    }
}