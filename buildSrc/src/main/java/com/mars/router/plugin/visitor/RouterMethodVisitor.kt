package com.mars.router.plugin.visitor

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

/**
 * Created by JohnnySwordMan on 2022/1/9
 */
class RouterMethodVisitor(
    api: Int,
    methodVisitor: MethodVisitor,
    private val routerSet: Set<String>?
) : MethodVisitor(api, methodVisitor) {

    override fun visitInsn(opcode: Int) {
        if (opcode >= Opcodes.IRETURN && opcode <= Opcodes.RETURN) {
            routerSet?.forEach { name ->
//                mv.visitMethodInsn(Opcodes.GETSTATIC, "com/mars/infra/router/runtime/Router", "routerMap", "Ljava/util/HashMap;")
                mv.visitFieldInsn(
                    Opcodes.GETSTATIC,
                    "com/mars/infra/router/runtime/Router",
                    "routerMap",
                    "Ljava/util/HashMap;"
                )
                // 是runtime/，不是runtime
                mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "com/mars/infra/router/runtime/$name",
                    "get",
                    "()Ljava/util/Map;"
                )
                mv.visitMethodInsn(
                    Opcodes.INVOKEVIRTUAL,
                    "java/util/HashMap",
                    "putAll",
                    "(Ljava/util/Map;)V"
                )
            }

            // 测试---start
            mv.visitTypeInsn(Opcodes.NEW, "com/mars/infra/router/LoginServiceImpl")
            mv.visitInsn(Opcodes.DUP)
            mv.visitMethodInsn(
                Opcodes.INVOKESPECIAL,
                "com/mars/infra/router/LoginServiceImpl",
                "<init>",
                "()V"
            )
            mv.visitVarInsn(Opcodes.ASTORE, 1)
            // 测试---end


            // isInit设置成true
            mv.visitInsn(Opcodes.ICONST_1)
            mv.visitFieldInsn(
                Opcodes.PUTSTATIC,
                "com/mars/infra/router/runtime/Router",
                "isInit",
                "Z"
            )
        }
        // 如果不加super，该方法内部的内容会消失，等于隐藏方法
        super.visitInsn(opcode)
    }
}
