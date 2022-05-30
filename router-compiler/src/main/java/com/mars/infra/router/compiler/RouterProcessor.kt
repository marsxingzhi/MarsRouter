package com.mars.infra.router.compiler

import com.mars.infra.router.api.*
import com.squareup.javapoet.*
import javax.annotation.processing.*
import javax.lang.model.SourceVersion
import javax.lang.model.element.Element
import javax.lang.model.element.Modifier
import javax.lang.model.element.TypeElement
import javax.lang.model.type.MirroredTypesException
import javax.lang.model.type.TypeMirror
import javax.lang.model.util.Elements
import javax.lang.model.util.Types

/**
 * Created by JohnnySwordMan on 2022/1/5
 */
class RouterProcessor : AbstractProcessor() {

    private var mFiler: Filer? = null
    private var mMessager: Messager? = null
    private var mElements: Elements? = null
    private var mTypes: Types? = null
    private var moduleName: String? = null


    private val annotations = setOf(
        RouterUri::class.java,
        Builder::class.java,
        Inject::class.java,
        ServiceImpl::class.java,
    )

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

        processingEnv?.options?.let {
            moduleName = it["moduleName"]
            println("APT---moduleName = $moduleName")
        }
    }


    override fun process(annotations: MutableSet<out TypeElement>, env: RoundEnvironment): Boolean {
        println("RouterProcessor---执行process方法, 当前 module name = $moduleName")
        handleRouterUriAnnotation(env)
        handleServiceImplAnnotation(env)
        BuilderAnnotationHandler(env).handle()
        return true
    }

    /**
     * 好像没法创建Map<String, ServiceImplData>类型的map
     */
    private fun handleServiceImplAnnotation(env: RoundEnvironment) {
        val codeBlockBuilder = CodeBlock.builder()
        codeBlockBuilder.addStatement(
            "\$T<\$T, \$T> map = new \$T()",
            Map::class.java, String::class.java, String::class.java, HashMap::class.java
        )
        env.getElementsAnnotatedWith(ServiceImpl::class.java)
            .filter { it.kind.isClass }
            .forEach { element ->
                // element = com.mars.infra.router.LoginServiceImpl
                println("handleServiceImplAnnotation element = $element")
                val annotation = element.getAnnotation(ServiceImpl::class.java)

                val interfaceClass =
                    try {
                        annotation.service[0].java
                    } catch (e: MirroredTypesException) {
                        e.typeMirrors[0]
                    }


//                val interfaceClass1 = element.getAnnotationClassValue<ServiceImpl> {
//                    this.service[0]
//                } as Class<*>

//                val implementClass = Class.forName(element.toString())
                val implementClass = element.toString()
                val singleton = annotation.singleton
                val data = ServiceImplData(interfaceClass.toString(), implementClass, singleton)
                println("ServiceImplData = $data")
                codeBlockBuilder.addStatement("map.put(\$S, \$S)", interfaceClass.toString(), implementClass)
            }
        ServiceImplOperate.buildServiceImplMapClass(mFiler, codeBlockBuilder.build(), moduleName!!)
    }

    private fun handleRouterUriAnnotation(env: RoundEnvironment) {
        // 放在forEach之前创建，不能在forEach里面创建，否则始终只有第一个代码块创建
        val codeBlockBuilder = CodeBlock.builder()
        val codeBlockBuilderForRouterMap = CodeBlock.builder()
        codeBlockBuilderForRouterMap.addStatement(
            "\$T<\$T, \$T> map = new \$T()",
            Map::class.java, String::class.java, String::class.java, HashMap::class.java
        )

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
                        //                        hash = Random.nextInt(10000).toString()
                    }
                    codeBlockBuilder.addStatement(
                        "handler.register(\$S, \$S, \$S)",
                        uri.module,
                        uri.path,
                        element
                    )
                    codeBlockBuilderForRouterMap.addStatement(
                        "map.put(\$S, \$S)",
                        uri.path,
                        element
                    )
                }
            }
        if (hash == null) {
            hash = "1024"
        }
        // 不能重复创建Java文件，因此这个需要放在forEach之后
        buildUriAnnotationInitClass(codeBlockBuilder.build(), hash)
        buildRouterMappingClass(codeBlockBuilderForRouterMap.build(), hash, moduleName!!)
    }

    /**
     * public class RouterMapping_xxx {
     *
     *
     *      public static Map<String, String> get() {
     *          Map<String, String> mapping = new HashMap<>();
     *          map.put("path", "com.mars.infra.router.LoginActivity");
     *          return map;
     *      }
     * }
     */
    private fun buildRouterMappingClass(codeBlock: CodeBlock?, hash: String, suffix: String) {
        val methodSpec =
            MethodSpec.methodBuilder("get")
                .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
                .addCode(codeBlock)
                .addStatement("return map")
                .returns(ClassName.get("java.util", "Map"))
                .build()

        val typeSpec =
            TypeSpec.classBuilder("RouterMapping_${hash}_$suffix")
                .addModifiers(Modifier.PUBLIC)
                .addMethod(methodSpec)
                .build()

        try {
            // javax.annotation.processing.FilerException: Attempt to recreate a file for type com.mars.infra.router.UriAnnotationInit_123456
            JavaFile.builder(PKG, typeSpec).build().writeTo(mFiler)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 生成以下代码
     * public class UriAnnotationInit_123 implements IUriAnnotationInit {
     *      public void init(UriHandler handler) {
     *          handler.register("module", "path", "com.mars.infra.router.LoginActivity")
     *      }
     * }
     *
     */
    private fun buildUriAnnotationInitClass(codeBlock: CodeBlock, hash: String? = null) {
        val simpleName = "UriAnnotationInit"
        val superInterfaceName = URI_ANNOTATION_INIT_CLASS

        val uriHandlerTypeElement = typeElement(URI_HANDLER_CLASS)?:return
        val superInterfaceTypeElement = typeElement(superInterfaceName)?:return

        val methodSpec =
            MethodSpec.methodBuilder(URI_ANNOTATION_INIT_METHOD)
                .addModifiers(Modifier.PUBLIC)
                .addParameter(ClassName.get(uriHandlerTypeElement), "handler")
                .addCode(codeBlock)
                .returns(TypeName.VOID)
                .build()


        val typeSpec =
            TypeSpec.classBuilder("${simpleName}_${hash}")
                .addModifiers(Modifier.PUBLIC)
                .addSuperinterface(ClassName.get(superInterfaceTypeElement))  // 如果typeElement方法崩溃，需要检查是否对应的className打到apk中
                .addMethod(methodSpec)
                .build()

        try {
            // javax.annotation.processing.FilerException: Attempt to recreate a file for type com.mars.infra.router.UriAnnotationInit_123456
            JavaFile.builder(PKG, typeSpec).build().writeTo(mFiler)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }


    fun isSubType(element: Element, className: String): Boolean {
        return mTypes!!.isSubtype(element.asType(), typeMirror(className))
    }

    private fun typeMirror(className: String): TypeMirror {
        return mElements!!.getTypeElement(className).asType()
    }

    // Caused by: java.lang.NullPointerException: mElements!!.getTypeElement(className) must not be null
    fun typeElement(className: String): TypeElement? {
        return mElements!!.getTypeElement(className)
    }

}