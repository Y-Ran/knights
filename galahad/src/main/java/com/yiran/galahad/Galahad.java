package com.yiran.galahad;

import com.yiran.galahad.annotation.Component;
import com.yiran.galahad.bean.ClassInfo;
import com.yiran.galahad.config.GalahadConfig;
import com.yiran.galahad.utils.ClassKit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Galahad {
    private static final Logger LOGGER = LoggerFactory.getLogger(Galahad.class);
    private static final GalahadConfig GALAHAD_CONFIG = new GalahadConfig();

    private ClassKit classKit = new ClassKit();

    public void init() {
        iocInit();
    }

    /**
     * ioc初始化
     */
    private void iocInit() {
        Set<ClassInfo> componentSet = new HashSet<>();

        Set<String> scanPackagePaths = GALAHAD_CONFIG.getScanPackages();
        // 获取所有component bean
        scanPackagePaths.forEach(scanPackagePath -> {
            try {
                Set<ClassInfo> subComponentSet = classKit.getClassInfo(scanPackagePath, null, Component.class, true);
                componentSet.addAll(subComponentSet);
            } catch (IOException e) {
                LOGGER.error("包扫描异常! scanPackagePath={}", scanPackagePath);
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
