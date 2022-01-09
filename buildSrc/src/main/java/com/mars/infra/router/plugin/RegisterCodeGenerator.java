package com.mars.infra.router.plugin;

import com.mars.infra.router.plugin.visitor.RouterClassVisitor;

import org.apache.commons.io.IOUtils;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.List;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarOutputStream;
import java.util.zip.ZipEntry;

/**
 * Created by JohnnySwordMan on 2022/1/8
 */
class RegisterCodeGenerator {

    public static void insertInitCode(List<String> registerList, File destFile) {

    }

    public static void insertInitCode(Set<String> registerMapping, File jarFile) throws IOException {
        File oprJar = new File(jarFile.getParent(), jarFile.getName() + ".opt");
        if (oprJar.exists()) {
            oprJar.delete();
        }
        JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(oprJar));
        // 操作Router.class所在的jar
        JarFile file = new JarFile(jarFile);
        Enumeration<JarEntry> entries = file.entries();
        while (entries.hasMoreElements()) {
            JarEntry jarEntry = entries.nextElement();
            String entryName = jarEntry.getName();
            ZipEntry zipEntry = new ZipEntry(entryName);
            InputStream inputStream = file.getInputStream(zipEntry);
            jarOutputStream.putNextEntry(zipEntry);
            if (entryName.equals(RouterMappingCollector.ROUTER_PATH)) {
                // 字节码插桩
                byte[] bytes = createCodeWhenInit(registerMapping, inputStream);
                jarOutputStream.write(bytes);
            } else {
                jarOutputStream.write(IOUtils.toByteArray(inputStream));
            }
//            jarOutputStream.write(IOUtils.toByteArray(inputStream));
            inputStream.close();
            jarOutputStream.closeEntry();
        }
        jarOutputStream.close();
        file.close();
        if (jarFile.exists()) {
            jarFile.delete();
        }
        oprJar.renameTo(jarFile);
    }

    /**
     * 插桩代码
     */
    private static byte[] createCodeWhenInit(Set<String> registerMapping, InputStream inputStream) throws IOException {
        ClassReader classReader = new ClassReader(inputStream);
        ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_FRAMES);
        RouterClassVisitor routerClassVisitor = new RouterClassVisitor(Opcodes.ASM5, classWriter, registerMapping);
        classReader.accept(routerClassVisitor, ClassReader.EXPAND_FRAMES);
        return classWriter.toByteArray();
    }
}
