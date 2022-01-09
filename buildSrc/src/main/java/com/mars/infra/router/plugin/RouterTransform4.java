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
import java.util.Collection;
import java.util.Set;

/**
 * Created by JohnnySwordMan on 2022/1/9
 */
class RouterTransform4 extends Transform {


    @Override
    public String getName() {
        return RouterTransform4.class.getName();
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

        RouterMappingCollector collector = new RouterMappingCollector();

        Collection<TransformInput> inputs = transformInvocation.getInputs();
        TransformOutputProvider outputProvider = transformInvocation.getOutputProvider();
        for (TransformInput input : inputs) {
            // apply该插件的模块的所有的类以directory的形式获取
            Collection<DirectoryInput> directoryInputs = input.getDirectoryInputs();
            if (directoryInputs != null && directoryInputs.size() > 0) {
                for (DirectoryInput dirInput: directoryInputs) {
                    File destDir = outputProvider.getContentLocation(
                            dirInput.getName(),
                            dirInput.getContentTypes(),
                            dirInput.getScopes(),
                            Format.DIRECTORY);
                    collector.collect(dirInput.getFile());
                    FileUtils.copyFile(dirInput.getFile(), destDir);
                }
            }
            // 本地依赖或者远程依赖，以jar包的形式获取
            Collection<JarInput> jarInputs = input.getJarInputs();
            if (jarInputs != null && jarInputs.size() > 0) {
                for (JarInput jarInput: jarInputs) {
                    File destFile = outputProvider.getContentLocation(
                            jarInput.getName(),
                            jarInput.getContentTypes(),
                            jarInput.getScopes(),
                            Format.JAR);
                    collector.collectFromJarFile2(jarInput.getFile());
                    FileUtils.copyFile(jarInput.getFile(), destFile);
                }
            }
        }
    }

}
