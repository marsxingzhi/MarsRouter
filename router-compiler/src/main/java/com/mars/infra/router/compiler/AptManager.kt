package com.mars.infra.router.compiler

import javax.annotation.processing.Filer
import javax.annotation.processing.Messager
import javax.annotation.processing.ProcessingEnvironment
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Created by JohnnySwordMan on 2/16/22
 */
object AptManager {

    lateinit var types: Types
    lateinit var elements: Elements
    lateinit var messager: Messager
    lateinit var filer: Filer

    fun init(env: ProcessingEnvironment){
        elements = env.elementUtils
        types = env.typeUtils
        messager = env.messager
        filer = env.filer
    }
}