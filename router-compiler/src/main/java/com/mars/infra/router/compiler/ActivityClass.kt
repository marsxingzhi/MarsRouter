package com.mars.infra.router.compiler

import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement

/**
 * Created by JohnnySwordMan on 2/18/22
 */
class ActivityClass(val typeElement: TypeElement) {

    val simpleName = typeElement.simpleName.toString()

    val packageName = typeElement.packageName()

    // 类的属性
    val fields = mutableListOf<Field>()

    // 是否是抽象类
    val isAbstract = typeElement.modifiers.contains(Modifier.ABSTRACT)

    val builder = ActivityClassBuilder(this)
}