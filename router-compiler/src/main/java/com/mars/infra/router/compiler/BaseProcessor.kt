package com.mars.infra.router.compiler

import com.mars.infra.router.api.Builder
import com.mars.infra.router.api.Inject
import com.mars.infra.router.api.RouterUri
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.TypeElement
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Created by JohnnySwordMan on 2022/1/5
 */
abstract class BaseProcessor : AbstractProcessor() {

    protected var mFiler: Filer? = null
    protected var mMessager: Messager? = null
    protected var mElements: Elements? = null
    protected var mTypes: Types? = null

    private val annotations = setOf(RouterUri::class.java, Builder::class.java, Inject::class.java)

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
        processingEnv?.let { AptManager.init(it) }
    }

    fun isSubType(element: Element, className: String): Boolean {
        return mTypes!!.isSubtype(element.asType(), typeMirror(className))
    }

    private fun typeMirror(className: String): TypeMirror {
        return mElements!!.getTypeElement(className).asType()
    }

    fun typeElement(className: String) : TypeElement {
        return mElements!!.getTypeElement(className)
    }
}