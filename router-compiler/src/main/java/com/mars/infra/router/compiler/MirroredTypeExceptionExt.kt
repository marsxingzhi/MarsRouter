//package com.mars.infra.router.compiler
//
//import javax.lang.model.element.Element
//import javax.lang.model.type.MirroredTypeException
//import javax.lang.model.type.MirroredTypesException
//import javax.lang.model.type.TypeMirror
//import kotlin.reflect.KClass
//
///**
// * Created by JohnnySwordMan on 2022/1/17
// */
//inline fun <reified T : Annotation> Element.getAnnotationClassValue(f: T.() -> KClass<*>): Class<*> =
//    try {
//        getAnnotation(T::class.java).f()
////        throw Exception("Expected to get a MirroredTypeException")
//    } catch (e: MirroredTypesException) {  // MirroredTypesException，不是MirroredTypeException
//        e.typeMirrors[0] as Class<*>
//    }
////    catch (e: MirroredTypeException) {
////        e.typeMirror
////    }
