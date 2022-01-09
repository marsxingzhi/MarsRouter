package com.mars.infra.router.plugin;

import com.android.build.gradle.AppExtension;

import org.gradle.api.Plugin;
import org.gradle.api.Project;

/**
 * Created by JohnnySwordMan on 2022/1/8
 * <p>
 * 插件作用
 * 1. 收集到com.mars.infra.router.UriAnnotationInit_hash类
 * 2. 在com.mars.infra.router.Router的loadRouterMap方法中，注入以下代码：
 *      new UriAnnotationInit_hash().init(handler)
 */
public class RouterPlugin implements Plugin<Project> {

    @Override
    public void apply(Project project) {
        System.out.println("RouterPlugin apply---😄");
        // AppExtension是AGP中的，因此需要引入com.android.tools.build:gradle:4.1.0
        AppExtension appExtension = project.getExtensions().getByType(AppExtension.class);
        appExtension.registerTransform(new RouterTransform4());
    }
}
