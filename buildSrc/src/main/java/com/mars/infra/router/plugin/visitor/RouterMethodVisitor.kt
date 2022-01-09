package com.mars.infra.router.plugin.visitor

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by JohnnySwordMan on 2022/1/9
 */
class RouterMethodVisitor(
    api: Int,
    methodVisitor: MethodVisitor,
    private val registerMapping: Set<String>?
) : MethodVisitor(api, methodVisitor) {

    override fun visitInsn(opcode: Int) {
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
            registerMapping?.forEach { name ->
                mv.visitTypeInsn(Opcodes.NEW, name)
                mv.visitInsn(Opcodes.DUP)
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, name, "<init>", "()V", false)
//                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitMethodInsn(Opcodes.INVOKESPECIAL, "com/mars/infra/router/Router", "getUriHandler", "()Lcom/mars/infra/router/UriHandler;", false)
                mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, name, "init", "(Lcom/mars/infra/router/UriHandler;)V", false)
            }
            // isInit设置成true
            mv.visitInsn(Opcodes.ICONST_1)
            mv.visitFieldInsn(Opcodes.PUTSTATIC, "com/mars/infra/router/Router", "isInit", "Z")
//            mv.visitMaxs(2, 1)
        }
        // 如果不加super，该方法内部的内容会消失，等于隐藏方法
        super.visitInsn(opcode)
    }
}