package com.yiran.galahad.utils;

import com.yiran.galahad.annotation.Component;
import com.yiran.galahad.bean.ClassInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.JarURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassKit {
    private static final String JAR_FILE = "jar:file:";
    private static final String WSJAR_FILE = "wsjar:file:";
    private static Logger LOGGER = LoggerFactory.getLogger(ClassKit.class);

    /**
     * Just test ClassUtils
     *
     * @param args args
     * @throws Exception Exception
     */
    public static void main(String[] args) throws Exception {
        ClassKit classKit = new ClassKit();
        Set<ClassInfo> classes = classKit.getAllClasses("com.yiran");
        LOGGER.info("start...");
        classes.forEach(clazz -> {
            LOGGER.info("class : {}", clazz.getClazzName());
        });
    }

    /**
     * 判断一个包名是否是jar包
     *
     * @param packageDir 包url
     * @return 是否为jar包
     */
    public static boolean isJarPackage(URL packageDir) {
        if (Objects.isNull(packageDir)) {
            return false;
        }
        try {
            // 判断URL中是否包含特定字符串
            String url = packageDir.toString();
            return url.contains(".jar!") || url.contains(".zip!");
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return false;
    }

    public Set<ClassInfo> getAllClasses(String packageName) throws IOException {
        return this.getClassInfo(packageName, null, null, true);
    }

    /**
     * 获取指定的类 封装为集合返回
     *
     * @param packageName 包名
     * @param parent      父类
     * @param annotation  注解
     * @param recursive   是否迭代
     * @return
     */
    public Set<ClassInfo> getClassInfo(String packageName, Class<?> parent, Class<? extends Annotation> annotation, boolean recursive) throws IOException {
        Set<ClassInfo> classInfoSet = new HashSet<>();
        packageName = packageName.replace(".", "/");
        // 获取所有的包对象
        Enumeration<URL> packageDirs = ClassKit.class.getClassLoader().getResources(packageName);
        while (packageDirs.hasMoreElements()) {
            URL packageDirUrl = packageDirs.nextElement();
            if (isJarPackage(packageDirUrl)) {
                classInfoSet.addAll(getClassInfoFromJar(packageDirUrl, packageName, parent, annotation, recursive));
                continue;
            }
            classInfoSet.addAll(getClassInfoFromClassPath(packageDirUrl, packageName, parent, annotation, recursive));
        }

        return classInfoSet;
    }

    private Set<ClassInfo> getClassInfoFromJar(URL packageDirUrl, String packageName, Class<?> parent, Class<? extends Annotation> annotation, boolean recursive) {
        Set<ClassInfo> classInfoSet = new HashSet<>();

        String packageDirName = packageName.replace(".", "/");
        try {
            Set<ClassInfo> subClassInfoSet = findClassByJar(packageDirUrl, packageDirName, packageName, parent, annotation, recursive);
            if (CollectionUtils.isNotEmpty(subClassInfoSet)) {
                classInfoSet.addAll(subClassInfoSet);
            }
        } catch (IOException | ClassNotFoundException e) {
            LOGGER.error("从jar包中扫描bean异常 packageDirUrl={}, packageName={}, parent={}, annotation={}, recursive={}",
                    packageDirUrl.toString(), packageDirName, parent.getName(), annotation.getName(), recursive);
        }
        return classInfoSet;
    }

    private Set<ClassInfo> findClassByJar(final URL url, final String packageDirName, String packageName, final Class<?> parent,
                                          final Class<? extends Annotation> annotation, final boolean recursive) throws IOException, ClassNotFoundException {
        Set<ClassInfo> subClassInfoSet = new HashSet<>();

        if (url.toString().startsWith(JAR_FILE) || url.toString().startsWith(WSJAR_FILE)) {
            // 获取jar文件对象
            JarFile jarFile = ((JarURLConnection) url.openConnection()).getJarFile();
            // 从jar包中获取文件
            Enumeration<JarEntry> jarEntryEnumeration = jarFile.entries();
            // 遍历jar包文件
            while (jarEntryEnumeration.hasMoreElements()) {
                JarEntry jarEntry = jarEntryEnumeration.nextElement();
                String name = jarEntry.getName();
                // 去掉 /
                if ("/".equals(name.charAt(0))) {
                    name = name.substring(1);
                }
                // 如果当前文件不是以packageDirName开头，说明不是要扫描的包文件
                if (!name.startsWith(packageDirName)) {
                    continue;
                }
                int idx = name.lastIndexOf("/");
                if (idx != -1) {
                    // 说明当前文件对象是子文件(夹) 获取对应包名
                    packageName = name.substring(0, idx).replace("/", ".");
                }
                // 如果是顶级目录同时不允许迭代
                if (idx == -1 && !recursive) {
                    continue;
                }
                // 如果文件不以.class结尾或者文件是文件夹 下一个
                if (!name.endsWith(".class") || jarEntry.isDirectory()) {
                    continue;
                }
                // 如果文件以.class结尾
                if (name.endsWith(".class")) {
                    String className = name.substring(packageName.length() + 1, name.length() - 6);
                    Class<?> clazz = Class.forName(packageName + "." + className);

                    // 如果父类和注解都不为null
                    if (ObjectUtils.allNotNull(parent, annotation)) {
                        // 如果当前类满足父类及枚举
                        if (ObjectUtils.allNotNull(clazz.getSuperclass(), clazz.getAnnotation(annotation)) && Objects.equals(parent, clazz.getSuperclass())) {
                            subClassInfoSet.add(new ClassInfo(clazz));
                            continue;
                        }
                    }
                    // 如果父类不为null
                    if (!Objects.isNull(parent)) {
                        if (!Objects.isNull(clazz.getSuperclass()) && Objects.equals(parent, clazz.getSuperclass())) {
                            subClassInfoSet.add(new ClassInfo(clazz));
                            continue;
                        }
                    }
                    // 如果注解不为空
                    if (!Objects.isNull(annotation)) {
                        if (!Objects.isNull(clazz.getAnnotation(annotation))) {
                            subClassInfoSet.add(new ClassInfo(clazz));
                            continue;
                        }
                    }
                    subClassInfoSet.add(new ClassInfo(clazz));
                }
            }
        }

        return subClassInfoSet;
    }

    private Set<ClassInfo> getClassInfoFromClassPath(URL packageDirUrl, String packageName, Class<?> parent, Class<? extends Annotation> annotation, boolean recursive) {
        Set<ClassInfo> classInfoSet = new HashSet<>();
        String packageDirName = packageName.replace("/", ".");
        try {
            String filePath = new URI(packageDirUrl.getFile()).getPath();
            Set<ClassInfo> subClassInfoSet = this.findClassByPackageAndFilePath(packageDirName, filePath, parent, annotation, recursive);
            if (CollectionUtils.isNotEmpty(subClassInfoSet)) {
                classInfoSet.addAll(subClassInfoSet);
            }

        } catch (IOException | URISyntaxException e) {
            LOGGER.error("从classPath中扫描bean异常 packageDirUrl={}, packageName={}, parent={}, annotation={}, recursive={}",
                    packageDirUrl.toString(), packageDirName, parent.getName(), annotation.getName(), recursive);
        } catch (ClassNotFoundException e) {
            LOGGER.error("class not fund", e);
        }

        return classInfoSet;
    }

    /**
     * 根据包名、包路径及其他参数获取类
     *
     * @param packageName 包名
     * @param filePath    包路径
     * @param parent      父类
     * @param annotation  注解
     * @param recursive   是否递归
     * @return 包下所有符合条件的类集合
     */
    private Set<ClassInfo> findClassByPackageAndFilePath(String packageName, String filePath, Class<?> parent,
                                                         Class<? extends Annotation> annotation, boolean recursive) throws IOException, ClassNotFoundException {
        Set<ClassInfo> subClassInfoSet = new HashSet<>();
        // 根据包路径创建文件(夹)对象
        File packageDir = new File(filePath);
        if (!packageDir.exists() || !packageDir.isDirectory()) {
            LOGGER.error("待扫描的包{}不存在 文件路径{}", packageName, packageDir.toString());
            return subClassInfoSet;
        }
        // 获得包路径下所有符合条件的文件(夹)
        File[] packageDirFiles = accept(packageDir, recursive);
        // 迭代
        if (Objects.isNull(packageDirFiles) || packageDirFiles.length == 0) {
            LOGGER.warn("包{}下没有符合条件的文件", packageName);
            return subClassInfoSet;
        }
        for (File file : packageDirFiles) {
            // 如果文件对象是文件夹 递归
            if (file.isDirectory()) {
                subClassInfoSet.addAll(findClassByPackageAndFilePath(packageName + "." + file.getName(), file.getCanonicalPath(), parent, annotation, recursive));
                continue;
            }
            // 如果文件是一个class文件
            String className = file.getName().substring(0, file.getName().length() - 6);
            Class<?> clazz = Class.forName(packageName + "." + className);
            // 如果父类和注解都不为null
            if (ObjectUtils.allNotNull(parent, annotation)) {
                // 如果当前类满足父类及枚举
                Class<?> parentClass = clazz.getSuperclass();
                Annotation anno = clazz.getAnnotation(annotation);
                if (ObjectUtils.allNotNull(parentClass, anno) && Objects.equals(parent, parentClass)) {
                    subClassInfoSet.add(new ClassInfo(anno, clazz));
                }
            } else if (!Objects.isNull(parent)) {
                // 如果父类不为null
                if (!Objects.isNull(clazz.getSuperclass()) && Objects.equals(parent, clazz.getSuperclass())) {
                    subClassInfoSet.add(new ClassInfo(clazz));
                }
            } else if (!Objects.isNull(annotation)) {
                // 如果注解不为空
                Annotation anno = clazz.getAnnotation(annotation);
                if (!Objects.isNull(anno)) {
                    subClassInfoSet.add(new ClassInfo(anno, clazz));
                }
            } else {
                subClassInfoSet.add(new ClassInfo(clazz));
            }
        }

        return subClassInfoSet;
    }

    /**
     * 判断文件(夹)是否符合条件
     *
     * @param file
     * @param recursive
     * @return
     */
    private File[] accept(File file, final boolean recursive) {
        // 传入文件(夹)只能包含文件夹及.class结尾的文件
        return file.listFiles(file1 -> (recursive && file1.isDirectory()) || (file1.getName().endsWith(".class")));
    }

}
