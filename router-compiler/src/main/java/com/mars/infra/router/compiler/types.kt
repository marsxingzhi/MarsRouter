package com.mars.infra.router.compiler

import com.squareup.javapoet.ClassName
import com.squareup.javapoet.TypeName
import javax.lang.model.element.ElementKind
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror

// 因为java lib中是没有android相关的库的，因此这里需要通过className，拿到对应的TypeName
val CONTEXT = ClassType("android.content.Context")
val INTENT = ClassType("android.content.Intent")
val TYPE_ACTIVITY_BUILDER = ClassType("com.mars.infra.router.runtime.builder.ActivityBuilder")
val TYPE_ACTIVITY = ClassType("android.app.Activity")
val BUNDLE = ClassType("android.os.Bundle")

/**
 * className ---> TypeElement ---> TypeMirror
 */
fun TypeMirror.isSubType(className: String): Boolean {
    val typeElement = AptManager.elements.getTypeElement(className)
    val typeMirror = typeElement.asType()
    return AptManager.types.isSubtype(this, typeMirror)
}

fun typeNameForString(): TypeName {
    return ClassName.get("java.lang", "String")
}

fun TypeElement.packageName(): String {
    var element = this.enclosingElement
    while (element != null && element.kind != ElementKind.PACKAGE) {
        element = element.enclosingElement
    }
    return element?.asType()?.toString() ?: throw IllegalArgumentException("$this does not have an enclosing element of package.")
}

fun TypeMirror.asJavaTypeName() = TypeName.get(this)
