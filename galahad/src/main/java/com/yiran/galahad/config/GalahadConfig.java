package com.yiran.galahad.config;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;

import static com.yiran.galahad.config.GalahadConstant.DEFAULT_PACKAGE_NAME;


public class GalahadConfig {

    private Set<String> scanPackages = new LinkedHashSet<>(DEFAULT_PACKAGE_NAME);


    public Set<String> getScanPackages() {
        return scanPackages;
    }

    public void setScanPackages(Set<String> scanPackages) {
        this.scanPackages = scanPackages;
    }
}
