package com.mars.infra.router.compiler

import com.squareup.javapoet.TypeName
import javax.lang.model.type.TypeMirror

/**
 * Created by JohnnySwordMan on 2/18/22
 */
class ClassType(private val className: String) {

    private val typeMirror: TypeMirror by lazy {
        AptManager.elements.getTypeElement(className).asType()
    }

    // 将className转换成TypeName
    val java by lazy {
        TypeName.get(typeMirror)
    }

}