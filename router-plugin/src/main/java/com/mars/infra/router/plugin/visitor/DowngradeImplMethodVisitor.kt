package com.mars.infra.router.plugin.visitor

import com.mars.infra.router.plugin.base.DowngradeImplData
import com.mars.infra.router.plugin.base.DowngradeImplManager
import com.mars.infra.router.plugin.base.ServiceImplData
import com.mars.infra.router.plugin.base.ServiceImplManager
import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by JohnnySwordMan on 2022/1/27
 */
class DowngradeImplMethodVisitor (
    api: Int,
    methodVisitor: MethodVisitor
) : MethodVisitor(api, methodVisitor) {

    private fun inject(isStart: Boolean, isLast: Boolean, data: DowngradeImplData) {
        val label = Label()
        mv.visitVarInsn(Opcodes.ALOAD, 0)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/Class",
            "getName",
            "()Ljava/lang/String;",
            false
        )
        mv.visitLdcInsn(data.interfaceClass)
        mv.visitMethodInsn(
            Opcodes.INVOKEVIRTUAL,
            "java/lang/String",
            "equals",
            "(Ljava/lang/Object;)Z",
            false
        )
        mv.visitJumpInsn(Opcodes.IFEQ, label)
        mv.visitTypeInsn(Opcodes.NEW, data.implementClass)
        mv.visitInsn(Opcodes.DUP)
        mv.visitMethodInsn(
            Opcodes.INVOKESPECIAL,
            data.implementClass,
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

            val serviceDataList = DowngradeImplManager.getDataList()
            for (i in serviceDataList.indices) {
                val data: DowngradeImplData = serviceDataList[i]
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