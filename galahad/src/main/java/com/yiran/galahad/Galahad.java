package com.yiran.galahad;

import com.yiran.galahad.annotation.Bean;
import com.yiran.galahad.annotation.Component;
import com.yiran.galahad.bean.ClassInfo;
import com.yiran.galahad.config.GalahadConfig;
import com.yiran.galahad.utils.ClassKit;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Galahad {
    private static final Logger LOGGER = LoggerFactory.getLogger(Galahad.class);
    private static final GalahadConfig GALAHAD_CONFIG = new GalahadConfig();

    private ClassKit classKit = new ClassKit();

    private Ioc ioc = new IocImpl();

    public void init() {
        iocInit();
    }

    /**
     * ioc初始化
     */
    private void iocInit() {

        Set<String> scanPackagePaths = GALAHAD_CONFIG.getScanPackages();

        // 获取所有component bean
        Set<ClassInfo> componentSet = new HashSet<>();
        scanPackagePaths.forEach(scanPackagePath -> {
            try {
                Set<ClassInfo> subComponentSet = classKit.getClassInfo(scanPackagePath, null, Component.class, true);
                componentSet.addAll(subComponentSet);
            } catch (IOException e) {
                LOGGER.error("包扫描异常! scanPackagePath={}", scanPackagePath);
            }
        });
        // 遍历所有的component set 创建相关对象并添加到ioc容器中
        componentSet.forEach(componentClassInfo -> {
            try {
                Class<?> clazz = componentClassInfo.getClazz();
                // 获取与类相关的注解
                Component componentAnno = (Component) componentClassInfo.getAnnotation();
                String annoName = componentAnno.name();
                // 创建对象
                Object componentBean = clazz.newInstance();
                String componentName;
                // 取bean名字
                if (StringUtils.isBlank(annoName)) {
                    String classSimpleName = clazz.getSimpleName();
                    componentName = classSimpleName.substring(0, 1).toLowerCase() + classSimpleName.substring(1);
                } else {
                    componentName = annoName;
                }
                // 添加到ioc容器中
                ioc.addBean(componentName, componentBean);
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("根据class创建object异常 componentClassInfo={}", componentClassInfo);
            }
        });

        // TODO 这里要改一下 不能通过扫描获取bean注解 要通过上面拿到的component set获取其中的bean注解方法 换句话说 bean注解只能用在component标注的类中
        // 获取所有的bean bean
        Set<ClassInfo> beanSet = new HashSet<>();
        scanPackagePaths.forEach(scanPackagePath -> {
            try {
                Set<ClassInfo> subBeanSet = classKit.getClassInfo(scanPackagePath, null, Bean.class, true);
                beanSet.addAll(subBeanSet);
            } catch (IOException e) {
                LOGGER.error("包扫描异常! scanPackagePath={}", scanPackagePath);
            }
        });
        // 对于每一个bean类 扫描其所有方法 获得Bean注解的方法
        beanSet.forEach(beanClassInfo -> {
            try {
                Class<?> clazz = beanClassInfo.getClazz();
                // 取注解名字
                Bean beanAnno = (Bean) beanClassInfo.getAnnotation();
                String annoName = beanAnno.name();
                // 获取所有方法
                Method[] allMethods = clazz.getDeclaredMethods();
                for (Method method : allMethods) {
                    // 如果某方法被Bean注解标记
                    if (method.isAnnotationPresent(Bean.class)) {
                        Class<?> returnClass = method.getReturnType();
                        Object returnBean = returnClass.newInstance();

                    }
                }
            } catch (InstantiationException | IllegalAccessException e) {
                LOGGER.error("根据class创建object异常 beanClassInfo={}", beanClassInfo);
            }
        });

    }

    /**
     * 创建Galahad对象
     *
     * @return Galahad对象
     */
    public static Galahad of() {
        return of(null);
    }

    /**
     * 创建Galahad对象
     *
     * @param configMap 键值对配置
     * @return Galahad对象
     */
    public static Galahad of(Map<String, String> configMap) {
        return new Galahad();
    }

    /**
     * 指定扫描包名
     *
     * @param packages 扫描目标包名
     * @return Galahad对象
     */
    public Galahad setScanPackages(String... packages){
        GALAHAD_CONFIG.getScanPackages().addAll(Arrays.asList(packages));
        return this;
    }

}
