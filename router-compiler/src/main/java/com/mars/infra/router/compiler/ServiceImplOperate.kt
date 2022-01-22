package com.mars.infra.router.compiler

import com.squareup.javapoet.*
import javax.annotation.processing.Filer
import javax.lang.model.element.Modifier

/**
 * Created by JohnnySwordMan on 2022/1/17
 */
object ServiceImplOperate {

    fun buildServiceImplMapClass(filer: Filer?, codeBlock: CodeBlock) {

        val methodSpec = MethodSpec.methodBuilder("collectServiceImplMap")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .addCode(codeBlock)
            .addStatement("return map")
            .returns(ClassName.get("java.util", "Map"))
            .build()

        val typeSpec = TypeSpec.classBuilder("ServiceImplMap_1024")
            .addModifiers(Modifier.PUBLIC)
            .addMethod(methodSpec)
            .build()

        try {
            JavaFile.builder(PKG, typeSpec).build().writeTo(filer)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}