package com.mars.router.plugin

import com.android.build.gradle.AppExtension
import org.gradle.api.Plugin
import org.gradle.api.Project


/**
 * Created by JohnnySwordMan on 2022/1/9
 */
class RouterPlugin: Plugin<Project> {

    override fun apply(project: Project) {
        println("😄---RouterPlugin apply---☺️")
        // AppExtension是AGP中的，因此需要引入com.android.tools.build:gradle:4.1.0
        val appExtension = project.extensions.getByType(AppExtension::class.java)
        appExtension.registerTransform(RouterTransform())
    }
}