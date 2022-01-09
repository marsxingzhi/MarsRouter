package com.mars.infra.router.plugin;

import com.android.build.api.transform.DirectoryInput;
import com.android.build.api.transform.Format;
import com.android.build.api.transform.JarInput;
import com.android.build.api.transform.QualifiedContent;
import com.android.build.api.transform.Transform;
import com.android.build.api.transform.TransformException;
import com.android.build.api.transform.TransformInput;
import com.android.build.api.transform.TransformInvocation;
import com.android.build.api.transform.TransformOutputProvider;
import com.android.build.gradle.internal.pipeline.TransformManager;
import com.android.utils.FileUtils;
import com.mars.infra.router.plugin.visitor.RouterClassVisitor;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * Created by JohnnySwordMan on 2022/1/8
 */
class RouterTransform extends Transform {

    // 所有注册类
//    private static List<String> registerList = new ArrayList<>();
    // Router.class所在的jar包
//    private static File destFile;

    @Override
    public String getName() {
        return RouterTransform.class.getSimpleName();
    }

    /**
     * 处理类型
     */
    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

    /**
     * 处理范围
     */
    @Override
    public Set<? super QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_PROJECT;
    }

    @Override
    public boolean isIncremental() {
        return false;
    }

    @Override
    public void transform(TransformInvocation transformInvocation) throws TransformException, InterruptedException, IOException {
        super.transform(transformInvocation);

        if (transformInvocation == null) {
            return;
        }

        if (!transformInvocation.isIncremental()) {
            transformInvocation.getOutputProvider().deleteAll();
        }

        Collection<TransformInput> inputs = transformInvocation.getInputs();
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        for (TransformInput input : inputs) {
            // apply该插件的模块的所有的类以directory的形式获取
            Collection<DirectoryInput> directoryInputs = input.getDirectoryInputs();
            handleDirectory(directoryInputs, outputProvider);
            // 本地依赖或者远程依赖，以jar包的形式获取
            Collection<JarInput> jarInputs = input.getJarInputs();
            handleJar(jarInputs, outputProvider);
        }
        // RouterTransform all mapping className = [UriAnnotationInit_996, UriAnnotationInit_2677]
        System.out.println("RouterTransform all mapping className = " + RouterMappingCollector.getRouterMapping());
        if (RouterMappingCollector.getDestFileOfRouter() != null) {
//            RegisterCodeGenerator.insertInitCode(registerList, destFile);
//            RegisterCodeGenerator.insertInitCode(RouterMappingCollector.getRouterMapping(), RouterMappingCollector.getDestFileOfRouter());
        }

//        File mappingJarFile = outputProvider.getContentLocation(
//                "router_mapping",
//                getOutputTypes(),
//                getScopes(),
//                Format.JAR);
//        System.out.println("RouterTransform mappingJarFile = " + mappingJarFile.getAbsolutePath());
//        if (mappingJarFile.getParentFile().exists()) {
//            mappingJarFile.getParentFile().mkdirs();
//        }
//        if (mappingJarFile.exists()) {
//            mappingJarFile.delete();
//        }

        // 将mappingJarFile写入本地
//        FileOutputStream fileOutputStream = new FileOutputStream(mappingJarFile);
//        JarOutputStream jarOutputStream = new JarOutputStream(fileOutputStream);
//        ZipEntry zipEntry = new ZipEntry(RouterMappingByteCodeBuilder.CLASS_NAME + ".class");
//        jarOutputStream.putNextEntry(zipEntry);
//        jarOutputStream.write(RouterMappingByteCodeBuilder.get(RouterMappingCollector.getRouterMapping()));
//        jarOutputStream.closeEntry();
//        jarOutputStream.close();
//        fileOutputStream.close();
    }


    /**
     * 遍历class文件，找到com.mars.infra.router.UriAnnotationInit_996
     */
    private void handleDirectory(Collection<DirectoryInput> directoryInputs, TransformOutputProvider outputProvider) throws IOException {
        for (DirectoryInput input : directoryInputs) {
            File destFile = outputProvider.getContentLocation(
                    input.getFile().getAbsolutePath(),
                    input.getContentTypes(),
                    input.getScopes(),
                    Format.DIRECTORY);
//            RouterMappingCollector.collectFromDirectory(input.getFile());
            FileUtils.copyFile(input.getFile(), destFile);
        }
    }

    private void handleJar(Collection<JarInput> jarInputs, TransformOutputProvider outputProvider) throws IOException {
        for (JarInput input : jarInputs) {
            File destFile = outputProvider.getContentLocation(
                    input.getFile().getAbsolutePath(),
                    input.getContentTypes(),
                    input.getScopes(),
                    Format.JAR);
//            RouterMappingCollector.collectFromJarFile(input.getFile());
            FileUtils.copyFile(input.getFile(), destFile);
        }
    }

    private void processDir(File input, File dest) throws IOException {
        if (input == null || dest == null) {
            return;
        }
        if (dest.exists()) {
            FileUtils.delete(dest);
        }
        FileUtils.mkdirs(dest);
        String srcDirPath = input.getAbsolutePath();
        String destDirPath = dest.getAbsolutePath();
        File[] files = input.listFiles();
        if (files.length > 0) {
            for (File file : files) {
                String destFilePath = file.getAbsolutePath().replace(srcDirPath, destDirPath);
                File destFile = new File(destFilePath);
                if (file.isDirectory()) {
                    processDir(file, destFile);
                } else if (file.isFile()) {
                    System.out.println("RouterTransform---processDir---srcDirPath = " + file.getAbsolutePath());
                    System.out.println("RouterTransform---processDir---destDirPath = " + destFile.getAbsolutePath());
//                    weave(file.getAbsolutePath(), destFile.getAbsolutePath());
                    FileUtils.copyFile(file, destFile);
                }
            }
        }
    }

    private void weave(String inputPath, String outputPath) {
        System.out.println("RouterTransform---weave---start---");
        System.out.println("RouterTransform---weave---inputPath = " + inputPath);
        System.out.println("RouterTransform---weave---outputPath = " + outputPath);
        try {
            FileInputStream fileInputStream = new FileInputStream(inputPath);
            ClassReader classReader = new ClassReader(fileInputStream);
            ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
            RouterClassVisitor routerClassVisitor = new RouterClassVisitor(Opcodes.ASM7, classWriter, null);
            classReader.accept(routerClassVisitor, ClassReader.EXPAND_FRAMES);
            FileOutputStream fileOutputStream = new FileOutputStream(new File(outputPath));
            fileOutputStream.write(classWriter.toByteArray());
            fileOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("RouterTransform---weave---end---");
    }


    private void processJar(File input, File dest) throws IOException {
        if (input == null || dest == null) {
            return;
        }
        if (dest.exists()) {
            FileUtils.delete(dest);
        }
        FileUtils.mkdirs(dest);
        String srcDirPath = input.getAbsolutePath();
        String destDirPath = dest.getAbsolutePath();
        File[] files = input.listFiles();
        if (files.length > 0) {
            for (File file : files) {
                String destFilePath = file.getAbsolutePath().replace(srcDirPath, destDirPath);
                File destFile = new File(destFilePath);
                if (file.isDirectory()) {
                    processJar(file, destFile);
                } else if (file.isFile()) {
                    // file.getAbsolutePath(), destFile.getAbsolutePath()
                    System.out.println("RouterTransform---processJar---inputPath = " + file.getAbsolutePath());
                    System.out.println("RouterTransform---processJar---outputPath = " + destFile.getAbsolutePath());
                }
            }
        }
    }
}
