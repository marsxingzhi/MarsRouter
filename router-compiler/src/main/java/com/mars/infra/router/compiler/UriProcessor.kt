package com.mars.infra.router.compiler

import com.mars.infra.router.api.RouterUri
import com.squareup.javapoet.*
import javax.annotation.processing.RoundEnvironment
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import kotlin.random.Random

/**
 * Created by JohnnySwordMan on 2022/1/5
 */
class UriProcessor : BaseProcessor() {

    override fun process(annotations: MutableSet<out TypeElement>, env: RoundEnvironment): Boolean {
        // 放在forEach之前创建，不能在forEach里面创建，否则始终只有第一个代码块创建
        val codeBlockBuilder = CodeBlock.builder()
        var hash: String? = null
        env.getElementsAnnotatedWith(RouterUri::class.java)
            .filter { it.kind.isClass }
            .forEach { element ->
                println("UriProcessor---element = ${element.simpleName}")
                if (isSubType(element, ACTIVITY)) {
                    println("UriProcessor---$element 是Activity的子类")
                    val uri = element.getAnnotation(RouterUri::class.java)
                    if (hash == null) {
                        // 不为空才创建，否则会出现一个模块存在多个文件
                        hash = Random.nextInt(10000).toString()
                    }
                    codeBlockBuilder.addStatement(
                        "handler.register(\$S, \$S, \$S)",
                        uri.module,
                        uri.path,
                        element
                    )


                }
            }
        if (hash == null) {
            hash = "996"
        }
        // 不能重复创建Java文件，因此这个需要放在forEach之后
        buildUriAnnotationInitClass(codeBlockBuilder.build(), hash)
        return true
    }

    /**
     * 生成以下代码
     * public class UriAnnotationInit_123 implements IUriAnnotationInit {
     *      public void init(UriHandler handler) {
     *          handler.register("module", "path", "com.mars.infra.router.LoginActivity")
     *      }
     * }
     */
    private fun buildUriAnnotationInitClass(codeBlock: CodeBlock, hash: String? = null) {
        val simpleName = "UriAnnotationInit"
        val superInterfaceName = URI_ANNOTATION_INIT_CLASS

        val methodSpec =
            MethodSpec.methodBuilder(URI_ANNOTATION_INIT_METHOD)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(typeElement(URI_HANDLER_CLASS)), "handler")
                .addCode(codeBlock)
                .returns(TypeName.VOID)
                .build()

        val typeSpec =
            TypeSpec.classBuilder("${simpleName}_${hash}")
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