package com.mars.infra.router.compiler

import com.mars.infra.router.api.Builder
import com.mars.infra.router.api.Inject
import com.sun.tools.javac.code.Symbol
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement

/**
 * Created by JohnnySwordMan on 2/18/22
 */
class BuilderAnnotationHandler(private val env: RoundEnvironment) {

    fun handle() {
        val map = HashMap<Element, ActivityClass>()
        env.getElementsAnnotatedWith(Builder::class.java)
            .filter { it.kind.isClass }  // Builder只能标注类
            .forEach { element ->
                // 日志打印：BuilderProcessor element = MainActivity
                val type = element.asType()
                when {
                    type.isSubType("android.app.Activity") -> {
                        map[element] = ActivityClass(element as TypeElement)
                    }
                }

            }
        env.getElementsAnnotatedWith(Inject::class.java)
            .filter { it.kind.isField }
            .forEach { element ->
                // 从这里可以看出Builder和Inject是绑定的
                map[element.enclosingElement]?.fields?.add(Field(element as Symbol.VarSymbol))
            }
        // 写到文件中
        map.values.forEach {
            it.builder.build(AptManager.filer)
        }
    }

}