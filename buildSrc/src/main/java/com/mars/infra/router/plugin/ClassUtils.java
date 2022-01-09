package com.mars.infra.router.plugin;

/**
 * Created by JohnnySwordMan on 2022/1/8
 */
public class ClassUtils {

    // copy form WMRouter
    public static String path2Classname(String entryName) {
        return entryName.replace(".class", "")
                .replace('\\', '.')
                .replace('/', '.');
    }
}
