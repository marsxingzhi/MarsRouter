package com.mars.infra.router.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * Created by JohnnySwordMan on 2022/1/10
 */
class MarsRouterPlugin : Plugin<Project> {

    override fun apply(project: Project) {
        println("MarsRouterPlugin apply >>> 😄")
        val appExtension = project.extensions.getByType(AppExtension::class.java)
        appExtension.registerTransform(RouterTransform())
    }
}