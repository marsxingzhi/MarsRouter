package com.mars.infra.router.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Created by JohnnySwordMan on 2022/1/9
 */
public class RouterMappingCollector {

    private static final String PKG = "com/mars/infra/router";
    private static final String CLASS_NAME_PREFIX = "UriAnnotationInit_";
    private static final String CLASS_NAME_SUFFIX = ".class";
    public static final String ROUTER_PATH = "com/mars/infra/router/Router.class";

    /**
     * 存储的是apt生成的UriAnnotationInit_xxx的类名
     */
    private static Set<String> routerMapping = new HashSet<>();
    private final Set<String> mappingClassNames = new HashSet<>();
    /**
     * Router.class所在的jar包
     */
    private static File destFile;

    public static Set<String> getRouterMapping() {
        return routerMapping;
    }

    public static File getDestFileOfRouter() {
        return destFile;
    }

    public static void collectFromJarFile(File jarFile) throws IOException {
        Enumeration<JarEntry> entries = new JarFile(jarFile).entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String entryName = jarEntry.getName();
            // entryName = androidx/arch/core/R.class
            // RouterMappingCollector collectFromJarFile entryName = com/mars/infra/router/Router.class
            System.out.println("RouterMappingCollector collectFromJarFile entryName = " + entryName);
            if (entryName.contains(PKG)
                    && entryName.contains(CLASS_NAME_PREFIX)
                    && entryName.contains(CLASS_NAME_SUFFIX)) {
                String className = entryName.replace(PKG, "")
                        .replace("/", "")
                        .replace(CLASS_NAME_SUFFIX, "");
                System.out.println("RouterMappingCollector collectFromJarFile className = " + className);
                routerMapping.add(className);
            }
            if (entryName.equals(ROUTER_PATH)) {
                System.out.println("RouterMappingCollector collectFromJarFile 找到Router文件了，entryName = " + entryName);
                System.out.println("RouterMappingCollector collectFromJarFile 找到Router文件了，file = " + jarFile.getAbsolutePath());
                destFile = jarFile;
            }
        }
    }

    /**
     * 找到apt生成的com.mars.infra.router.UriAnnotationInit_xxx这个类
     */
    public static void collectFromDirectory(File file) {
        if (file == null || !file.exists()) {
            return;
        }
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File child : files) {
                    collectFromDirectory(child);
                }
            }
        } else {
            System.out.println("RouterMappingCollector collectFromDirectory file = " + file.getAbsolutePath());
            // app/build/intermediates/javac/debug/classes/com/mars/infra/router/UriAnnotationInit_531.class
            if (file.getAbsolutePath().contains(PKG)
                    && file.getName().startsWith(CLASS_NAME_PREFIX)
                    && file.getName().endsWith(CLASS_NAME_SUFFIX)) {
                String className = file.getName().replace(CLASS_NAME_SUFFIX, "");
                System.out.println("RouterMappingCollector collectFromDirectory className = " + className);
                routerMapping.add(className);
            }
        }
    }


    public void collect(File classFile) {
        if (classFile == null || !classFile.exists()) return;
        if (classFile.isFile()) {
            if (classFile.getAbsolutePath().contains(PKG)
                    && classFile.getName().startsWith(CLASS_NAME_PREFIX)
                    && classFile.getName().endsWith(CLASS_NAME_SUFFIX)) {
                String className = classFile.getName().replace(CLASS_NAME_SUFFIX, "");
                mappingClassNames.add(className);
            }
        } else {
            for (File file : classFile.listFiles()) {
                collect(file);
            }
        }
    }

    public void collectFromJarFile2(File jarFile) throws IOException {
        Enumeration enumeration = new JarFile(jarFile).entries();
        while (enumeration.hasMoreElements()) {
            JarEntry jarEntry = (JarEntry) enumeration.nextElement();
            String entryName = jarEntry.getName();
            if (entryName.contains(PKG)
                    && entryName.contains(CLASS_NAME_PREFIX)
                    && entryName.contains(CLASS_NAME_SUFFIX)) {
                String className = entryName.replace(PKG, "")
                        .replace("/", "")
                        .replace(CLASS_NAME_SUFFIX, "");
                mappingClassNames.add(className);
            }
        }
    }
}
