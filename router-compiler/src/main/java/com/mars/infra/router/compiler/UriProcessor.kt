package com.mars.infra.router.compiler

import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.TypeElement

/**
 * Created by JohnnySwordMan on 2022/1/5
 */
class UriProcessor : BaseProcessor() {

    override fun process(
        annotations: MutableSet<out TypeElement>?,
        roundEnv: RoundEnvironment?
    ): Boolean {
        return false
    }
}