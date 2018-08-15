package com.yiran.galahad.bean;

import java.lang.annotation.Annotation;
import java.util.Objects;

public class ClassInfo {

    private Annotation annotation;

    private String clazzName;

    private Class<?> clazz;

    public ClassInfo(Class<?> clazz) {
        this.clazz = clazz;
        String clazzSimpleName = clazz.getSimpleName();
        this.clazzName = clazzSimpleName.substring(0, 1).toLowerCase() + clazzSimpleName.substring(1);
        this.annotation = null;
    }

    public ClassInfo(String clazzName, Class<?> clazz) {
        this.clazzName = clazzName;
        this.clazz = clazz;
        this.annotation = null;
    }

    public ClassInfo(Annotation annotation, String clazzName, Class<?> clazz) {
        this.annotation = annotation;
        this.clazzName = clazzName;
        this.clazz = clazz;
    }

    public ClassInfo(Annotation annotation, Class<?> clazz) {
        this.annotation = annotation;
        this.clazz = clazz;
    }

    public ClassInfo() {
    }

    public String getClazzName() {
        return clazzName;
    }

    public void setClazzName(String clazzName) {
        this.clazzName = clazzName;
    }

    public Class<?> getClazz() {
        return clazz;
    }

    public void setClazz(Class<?> clazz) {
        this.clazz = clazz;
    }

    public Annotation getAnnotation() {
        return annotation;
    }

    public void setAnnotation(Annotation annotation) {
        this.annotation = annotation;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClassInfo classInfo = (ClassInfo) o;
        return Objects.equals(clazzName, classInfo.clazzName) &&
                Objects.equals(clazz, classInfo.clazz);
    }

    @Override
    public int hashCode() {

        return Objects.hash(clazzName, clazz);
    }

    @Override
    public String toString() {
        return "ClassInfo{" +
                "clazzName='" + clazzName + '\'' +
                ", clazz=" + clazz +
                '}';
    }
}
