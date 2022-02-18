package com.mars.infra.router.compiler

import com.squareup.javapoet.MethodSpec
import com.squareup.javapoet.TypeName
import com.squareup.javapoet.TypeSpec
import javax.lang.model.element.Modifier

/**
 * Created by JohnnySwordMan on 2/18/22
 */
class InjectMethodBuilder(private val activityClass: ActivityClass) {

    fun build(typeBuilder: TypeSpec.Builder) {
        typeBuilder.addMethod(createInjectMethod())
    }

    /**
     * public staic void inject(Activity activity, Bundle savedInstanceState) {
     *      if (activity instanceOf XXActivity) {
     *          XXActivity instance = (XXActivity) activity;
     *          Bundle extra = savedInstanceState != null ? savedInstanceState: instance.getIntent().getExtra()
     *          if (extra != null) {
     *              instance.username = extra.getString("username")
     *              instance.password = extra.getString("password")
     *          }
     *      }
     * }
     */
    private fun createInjectMethod(): MethodSpec {
        val methodBuilder = MethodSpec.methodBuilder("inject")
            .addModifiers(Modifier.PUBLIC, Modifier.STATIC)
            .returns(TypeName.VOID)
            .addParameter(TYPE_ACTIVITY.java, "activity")
            .addParameter(BUNDLE.java, "savedInstanceState")
//            .addStatement("if (activity instanceOf \$S)", "LoginActivity")  addStatement是添加一条语句
            .beginControlFlow("if (activity instanceof \$T)", activityClass.typeElement)
            .addStatement("\$T instance = (\$T) activity", activityClass.typeElement, activityClass.typeElement)
            .addStatement("\$T extra = savedInstanceState != null ? savedInstanceState: instance.getIntent().getExtras()", BUNDLE.java)
            .beginControlFlow("if (extra != null)")

        activityClass.fields.forEach { field ->
            methodBuilder.addStatement("instance.\$L = extra.getString(\$S)", field.name, field.name)
        }
        methodBuilder
            .endControlFlow()
            .endControlFlow()
        return methodBuilder.build()
    }

}