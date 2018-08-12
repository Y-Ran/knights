package com.yiran.galahad;

public interface Ioc {

    /**
     * 向IOC容器中添加一个bean
     * @param bean bean
     */
    void addBean(Object bean);

    /**
     * 向IOC容器中添加一个bean
     * @param name bean名字
     * @param bean bean对象
     */
    void addBean(String name, Object bean);

    /**
     * 根据名字从ioc容器中获取bean
     *
     * @param name bean名字
     * @return bean对象
     */
    Object getBean(String name) throws IllegalAccessException;

    /**
     * 根据类型获取一个bean
     *
     * @param clazz class类型
     * @param <T> 类型
     * @return bean对象
     */
    <T> T getBean(Class<T> clazz) throws IllegalAccessException;
}
