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
import com.android.dx.util.ByteArray;
import com.android.utils.FileUtils;
import com.google.common.collect.FluentIterable;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

/**
 * Created by JohnnySwordMan on 2022/1/8
 */
public class RouterTransform3 extends Transform {

    private TransformOutputProvider outputProvider;

    @Override
    public String getName() {
        return RouterTransform3.class.getName();
    }

    @Override
    public Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS;
    }

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
        outputProvider = transformInvocation.getOutputProvider();
        if (!transformInvocation.isIncremental()) {
            outputProvider.deleteAll();
        }
        Collection<TransformInput> inputs = transformInvocation.getInputs();
        for (TransformInput input : inputs) {
            for (JarInput jarInput : input.getJarInputs()) {

            }
            for (DirectoryInput directoryInput : input.getDirectoryInputs()) {
                foreachClass(directoryInput);
            }
        }
    }

    private void foreachClass(DirectoryInput directoryInput) throws IOException {
        File dest = outputProvider.getContentLocation(
                directoryInput.getName(),
                directoryInput.getContentTypes(),
                directoryInput.getScopes(),
                Format.DIRECTORY);
        File dir = directoryInput.getFile();
        if (dir.isDirectory()) {
            FileUtils.copyDirectory(dir, dest);
            FluentIterable<File> allFiles = FileUtils.getAllFiles(dir);
            for (File classFile : allFiles) {
                // 只处理类，例如：MarsRouter/app/build/tmp/kotlin-classes/debug/META-INF/app_debug.kotlin_module
                if (classFile.getName().endsWith(".class")) {
                    String absolutePath = classFile.getAbsolutePath().replace(dir.getAbsolutePath() + File.separator, "");
                    String className = ClassUtils.path2Classname(absolutePath);
//                    process(className, null);
                }
            }
        }
    }

//    private ByteArray process(String className, ByteArray classBytes) {
//        if (!ClassNameFilter.INSTANCE.filter(className)) {
//            return processInner(className, classBytes);
//        }
//        return null;
//    }

//    private ByteArray processInner(String className, ByteArray classBytes) {
//
//    }


}
