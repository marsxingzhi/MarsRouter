package com.mars.infra.router.compiler

import com.mars.infra.router.api.RouterUri
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.TypeElement
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Created by JohnnySwordMan on 2022/1/5
 */
abstract class BaseProcessor : AbstractProcessor() {

    protected var mFiler: Filer? = null
    protected var mMessager: Messager? = null
    protected var mElements: Elements? = null
    private var mTypes: Types? = null

    private val annotations = setOf(RouterUri::class.java)

    override fun getSupportedSourceVersion(): SourceVersion {
        return SourceVersion.latestSupported()
    }

    override fun getSupportedAnnotationTypes(): MutableSet<String> {
        return annotations.mapTo(HashSet(), Class<*>::getCanonicalName)
    }

    override fun init(processingEnv: ProcessingEnvironment?) {
        super.init(processingEnv)
        mFiler = processingEnv?.filer
        mMessager = processingEnv?.messager
        mElements = processingEnv?.elementUtils
        mTypes = processingEnv?.typeUtils
    }
}