package com.mars.infra.router.compiler

import com.mars.infra.router.api.RouterUri
import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.tools.Diagnostic

/**
 * Created by JohnnySwordMan on 2022/1/5
 */
class UriProcessor : BaseProcessor() {

    override fun process(
        annotations: MutableSet<out TypeElement>,
        env: RoundEnvironment
    ): Boolean {
        env.getElementsAnnotatedWith(RouterUri::class.java)
            .filter { it.kind.isClass }
            .forEach { element ->
//                mMessager?.printMessage(
//                    Diagnostic.Kind.WARNING,
//                    "UriProcessor element = ${element.simpleName}"
//                )
                println("UriProcessor---element = ${element.simpleName}")
                if (isSubType(element, ACTIVITY)) {
//                    mMessager?.printMessage(Diagnostic.Kind.WARNING, "$element 是Activity的子类")
                    println("UriProcessor---$element 是Activity的子类")
                    buildUriAnnotationInitClass(element)
                }
            }
        return false
    }

    /**
     * 生成以下代码
     * public class UriAnnotationInit_123 implements IUriAnnotationInit {
     *      public void init(UriHandler handler) {
     *          handler.register("module", "path", "com.mars.infra.router.LoginActivity")
     *      }
     * }
     */
    private fun buildUriAnnotationInitClass(element: Element) {
        val simpleName = "UriAnnotationInit"
        val superInterfaceName = URI_ANNOTATION_INIT_CLASS

        val methodSpec =
            MethodSpec.methodBuilder(URI_ANNOTATION_INIT_METHOD)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(typeElement(URI_HANDLER_CLASS)), "handler")
                .returns(TypeName.VOID)
                .build()

        val typeSpec =
            TypeSpec.classBuilder("${simpleName}_123456")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(typeElement(superInterfaceName)))  // 如果typeElement方法崩溃，需要检查是否对应的className打到apk中
                .addMethod(methodSpec)
                .build()

        try {
            // javax.annotation.processing.FilerException: Attempt to recreate a file for type com.mars.infra.router.UriAnnotationInit_123456
            JavaFile.builder(PKG, typeSpec).build().writeTo(mFiler)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}