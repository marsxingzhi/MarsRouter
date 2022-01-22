package com.mars.infra.router.plugin.visitor

import org.objectweb.asm.Label
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.Type


class ServiceImplMethodVisitor(
    api: Int,
    methodVisitor: MethodVisitor,
    private val serviceImplSet: Set<String>?
) : MethodVisitor(api, methodVisitor) {

    override fun visitInsn(opcode: Int) {
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
            mv.visitCode()
            mv.visitVarInsn(Opcodes.ALOAD, 1)
            mv.visitLdcInsn("serviceClass")
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "kotlin/jvm/internal/Intrinsics",
                "checkNotNullParameter",
                "(Ljava/lang/Object;Ljava/lang/String;)V",
                false
            )
            mv.visitVarInsn(Opcodes.ALOAD, 1)
            mv.visitVarInsn(Opcodes.ASTORE, 2)
            mv.visitVarInsn(Opcodes.ALOAD, 2)
            mv.visitLdcInsn(Type.getType("Lcom/mars/infra/router/ILoginService;"))
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "kotlin/jvm/internal/Intrinsics",
                "areEqual",
                "(Ljava/lang/Object;Ljava/lang/Object;)Z",
                false
            )
            val label0 = Label()
            mv.visitJumpInsn(Opcodes.IFEQ, label0)
            mv.visitTypeInsn(Opcodes.NEW, "com/mars/infra/router/LoginServiceImpl")
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "com/mars/infra/router/LoginServiceImpl",
                "<init>",
                "()V",
                false
            )
            mv.visitVarInsn(Opcodes.ASTORE, 3)
            mv.visitVarInsn(Opcodes.ALOAD, 3)
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object")
            val label1 = Label()
            mv.visitJumpInsn(Opcodes.GOTO, label1)
            mv.visitLabel(label0)
            mv.visitVarInsn(Opcodes.ALOAD, 2)
            mv.visitLdcInsn(Type.getType("Lcom/mars/infra/router/IFakeService;"))
            mv.visitMethodInsn(
                Opcodes.INVOKESTATIC,
                "kotlin/jvm/internal/Intrinsics",
                "areEqual",
                "(Ljava/lang/Object;Ljava/lang/Object;)Z",
                false
            )
            val label2 = Label()
            mv.visitJumpInsn(Opcodes.IFEQ, label2)
            mv.visitTypeInsn(Opcodes.NEW, "com/mars/infra/router/FakeServiceImpl")
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "com/mars/infra/router/FakeServiceImpl",
                "<init>",
                "()V",
                false
            )
            mv.visitVarInsn(Opcodes.ASTORE, 3)
            mv.visitVarInsn(Opcodes.ALOAD, 3)
            mv.visitTypeInsn(Opcodes.CHECKCAST, "java/lang/Object")
            mv.visitJumpInsn(Opcodes.GOTO, label1)
            mv.visitLabel(label2)
            mv.visitInsn(Opcodes.ACONST_NULL)
            mv.visitLabel(label1)
            mv.visitInsn(Opcodes.ARETURN)
            mv.visitMaxs(2, 4)
            mv.visitEnd()
        }
        super.visitInsn(opcode)
    }
}