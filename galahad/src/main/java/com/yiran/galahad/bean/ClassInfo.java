package com.yiran.galahad.bean;

import java.util.Objects;

public class ClassInfo {

    private Class<?> clazz;

    public ClassInfo(Class<?> clazz) {
        this.clazz = clazz;
    }

    public ClassInfo() {
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassInfo classInfo = (ClassInfo) o;
        return Objects.equals(clazz, classInfo.clazz);
    }

    @Override
    public int hashCode() {

        return Objects.hash(clazz);
    }

    @Override
    public String toString() {
        return "ClassInfo{" +
                "clazz=" + clazz +
                '}';
    }
}
