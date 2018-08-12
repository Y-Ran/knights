package com.yiran.galahad;

import com.yiran.galahad.config.GalahadConfig;

import java.util.Arrays;
import java.util.Map;

public class Galahad {

    private static final GalahadConfig GALAHAD_CONFIG = new GalahadConfig();

    public void init() {
        iocInit();
    }

    /**
     * ioc初始化
     */
    private void iocInit() {

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
