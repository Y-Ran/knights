package com.yiran.galahad.utils;

import com.yiran.galahad.bean.ClassInfo;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.net.*;
import java.util.*;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class ClassKit {

    private static Logger LOGGER = LoggerFactory.getLogger(ClassKit.class);

    /**
     * Just test ClassUtils
     *
     * @param args args
     * @throws Exception Exception
     */
    public static void main(String[] args) throws Exception {
        Set<Class<?>> classes = getClasses("com.yiran.galahad");
        LOGGER.info("start...");
        classes.forEach(clazz -> {
            LOGGER.info("class : {}", clazz.getSimpleName());
        });
    }

    public Set<ClassInfo> getClassInfo (String packageName, Class<?> parent, Class<? extends Annotation> annotation, boolean recursive) {
        if (isJarPackage(packageName)) {
            return getClassInfoFromJar(packageName, parent, annotation, recursive);
        }

        return getClassInfoFromClassPath(packageName, parent, annotation, recursive);


    }


    private Set<ClassInfo> getClassInfoFromJar(String packageName, Class<?> parent, Class<? extends Annotation> annotation, boolean recursive) {



        Set<ClassInfo> classInfoSet = new HashSet<>();


        return classInfoSet;
    }

    private Set<ClassInfo> getClassInfoFromClassPath(String packageName, Class<?> parent, Class<? extends Annotation> annotation, boolean recursive) {
        Set<ClassInfo> classInfoSet =  new HashSet<>();
        String packageDirName = packageName.replace(".", "/");
        Enumeration<URL> urlEnumeration;
        try {
            urlEnumeration = this.getClass().getClassLoader().getResources(packageDirName);
            while (urlEnumeration.hasMoreElements()) {
                URL url = urlEnumeration.nextElement();
                String filePath = new URI(url.getFile()).getPath();
                Set<ClassInfo> subClassInfoSet = findClassByPackageAndFilePath(packageName, filePath, parent, annotation, recursive);
                if (CollectionUtils.isNotEmpty(subClassInfoSet)) {
                    classInfoSet.addAll(subClassInfoSet);
                }
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("", e);
        } catch (ClassNotFoundException e) {
            LOGGER.error("class not fund", e);
        }

        return classInfoSet;
    }

    /**
     *
     * 根据包名、包路径及其他参数获取类
     *
     * @param packageName 包名
     * @param filePath 包路径
     * @param parent 父类
     * @param annotation 注解
     * @param recursive 是否递归
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
                return subClassInfoSet;
            }
            // 如果文件是一个class文件
            String className = file.getName().substring(0, file.getName().length() - 6);
            Class<?> clazz = Class.forName(packageName + "." + className);
            // 如果父类和注解都不为null
            if (ObjectUtils.allNotNull(parent, annotation)) {
                // 如果当前类满足父类及枚举
                if (ObjectUtils.allNotNull(clazz.getSuperclass(), clazz.getAnnotation(annotation)) && Objects.equals(parent, clazz.getSuperclass())) {
                    subClassInfoSet.add(new ClassInfo(clazz));
                }
            }
            // 如果父类不为null
            if (!Objects.isNull(parent)) {
                if (!Objects.isNull(clazz.getSuperclass()) && Objects.equals(parent, clazz.getSuperclass())) {
                    subClassInfoSet.add(new ClassInfo(clazz));
                }
            }
            // 如果注解不为空
            if (!Objects.isNull(annotation)) {
                if (!Objects.isNull(clazz.getAnnotation(annotation))) {
                    subClassInfoSet.add(new ClassInfo(clazz));
                }
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


    /**
     * 从包package中获取所有的Class
     *
     * @param pack
     * @return
     */
    public static Set<Class<?>> getClasses(String pack) {

        // 第一个class类的集合
        Set<Class<?>> classes = new LinkedHashSet<>();
        // 是否循环迭代
        // 获取包的名字 并进行替换
        String packageName = pack;
        String packageDirName = packageName.replace('.', '/');
        // 定义一个枚举的集合 并进行循环来处理这个目录下的things
        Enumeration<URL> dirs;
        try {
            dirs = Thread.currentThread()
                    .getContextClassLoader()
                    .getResources(packageDirName);
            // 循环迭代下去
            while (dirs.hasMoreElements()) {
                // 获取下一个元素
                URL url = dirs.nextElement();
                // 得到协议的名称
                String protocol = url.getProtocol();
                // 如果是以文件的形式保存在服务器上
                if ("file".equals(protocol)) {
                    System.err.println("file类型的扫描");
                    // 获取包的物理路径
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // 以文件的方式扫描整个包下的文件 并添加到集合中
                    findAndAddClassesInPackageByFile(packageName,
                            filePath, true, classes);
                } else if ("jar".equals(protocol)) {
                    // 如果是jar包文件
                    // 定义一个JarFile
                    System.err.println("jar类型的扫描");
                    JarFile jar;
                    try {
                        // 获取jar
                        jar = ((JarURLConnection) url.openConnection()).getJarFile();
                        // 从此jar包 得到一个枚举类
                        Enumeration<JarEntry> entries = jar.entries();
                        // 同样的进行循环迭代
                        while (entries.hasMoreElements()) {
                            // 获取jar里的一个实体 可以是目录 和一些jar包里的其他文件 如META-INF等文件
                            JarEntry entry = entries.nextElement();
                            String name = entry.getName();
                            // 如果是以/开头的
                            if (name.charAt(0) == '/') {
                                // 获取后面的字符串
                                name = name.substring(1);
                            }
                            // 如果前半部分和定义的包名相同
                            if (name.startsWith(packageDirName)) {
                                int idx = name.lastIndexOf('/');
                                // 如果以"/"结尾 是一个包
                                if (idx != -1) {
                                    // 获取包名 把"/"替换成"."
                                    packageName = name.substring(0, idx).replace('/', '.');
                                }
                                // 如果可以迭代下去 并且是一个包
                                // 如果是一个.class文件 而且不是目录
                                if (name.endsWith(".class") && !entry.isDirectory()) {
                                    // 去掉后面的".class" 获取真正的类名
                                    String className = name.substring(
                                            packageName.length() + 1, name.length() - 6);
                                    try {
                                        // 添加到classes
                                        classes.add(Class.forName(packageName + '.' + className));
                                    } catch (ClassNotFoundException e) {
                                        // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } catch (IOException e) {
                        // log.error("在扫描用户定义视图时从jar包获取文件出错");
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    /**
     * 以文件的形式来获取包下的所有Class
     *
     * @param packageName
     * @param packagePath
     * @param recursive
     * @param classes
     */
    public static void findAndAddClassesInPackageByFile(String packageName,
                                                        String packagePath, final boolean recursive, Set<Class<?>> classes) {
        // 获取此包的目录 建立一个File
        File dir = new File(packagePath);
        // 如果不存在或者 也不是目录就直接返回
        if (!dir.exists() || !dir.isDirectory()) {
            // log.warn("用户定义包名 " + packageName + " 下没有任何文件");
            return;
        }
        // 如果存在 就获取包下的所有文件 包括目录
        // 自定义过滤规则 如果可以循环(包含子目录) 或则是以.class结尾的文件(编译好的java类文件)
        File[] dirfiles = dir.listFiles(file -> (recursive && file.isDirectory()) ||
                (file.getName().endsWith(".class")));
        // 循环所有文件
        for (File file : dirfiles) {
        // 如果是目录 则继续扫描
            if (file.isDirectory()) {
                findAndAddClassesInPackageByFile(packageName + "."
                                + file.getName(), file.getAbsolutePath(), recursive,
                        classes);
            } else {
                // 如果是java类文件 去掉后面的.class 只留下类名
                String className = file.getName().substring(0,
                        file.getName().length() - 6);
                try {
                    // 添加到集合中去
                    //classes.add(Class.forName(packageName + '.' + className));
                    //经过回复同学的提醒，这里用forName有一些不好，会触发static方法，
                    //没有使用classLoader的load干净
                    classes.add(Thread.currentThread()
                            .getContextClassLoader()
                            .loadClass(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    // log.error("添加用户自定义视图类错误 找不到此类的.class文件");
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 判断一个包名是否是jar包
     *
     * @param packageName 包名
     * @return 是否为jar包
     */
    public static boolean isJarPackage(String packageName) {
        if (StringUtils.isBlank(packageName)) {
            return false;
        }
        try {
            packageName = packageName.replace(".", "/");
            Enumeration<URL> dirs = ClassKit.class.getClassLoader().getResources(packageName);
            if (dirs.hasMoreElements()) {
                String url = dirs.nextElement().toString();
                return url.contains(".jar!") || url.contains(".zip!");
            }
        } catch (Exception e) {
            LOGGER.error("", e);
        }
        return false;
    }

}
