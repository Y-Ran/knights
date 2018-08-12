package com.yiran.galahad.bean;

public class BeanDefine {

    private String name;

    private Object bean;

    private Class<?> type;

    private boolean isSingle;

    public BeanDefine(Object bean) {
        this(bean, bean.getClass());
    }

    public BeanDefine(Object bean, Class<?> type) {
        this(type.getName(), bean, type, true);
    }

    public BeanDefine(String name, Object bean, Class<?> type, boolean isSingle) {
        this.name = name;
        this.bean = bean;
        this.type = type;
        this.isSingle = isSingle;
    }

    public Object getBean() {
        return bean;
    }

    public void setBean(Object bean) {
        this.bean = bean;
    }

    public Class<?> getType() {
        return type;
    }

    public void setType(Class<?> type) {
        this.type = type;
    }

    public boolean isSingle() {
        return isSingle;
    }

    public void setSingle(boolean single) {
        isSingle = single;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
