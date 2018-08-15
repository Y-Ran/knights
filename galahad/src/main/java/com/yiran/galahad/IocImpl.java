package com.yiran.galahad;

import com.yiran.galahad.bean.BeanDefine;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * ioc容器及相关操作
 */
public class IocImpl implements Ioc {
    private static final Logger LOGGER = LoggerFactory.getLogger(IocImpl.class);

//    private static final Map<String, BeanDefine> beanPool = new HashMap<>(32);

    @Override
    public void addBean(Object bean) {
        this.addBean(bean.getClass().getName(), bean);
    }

    @Override
    public void addBean(String name, Object bean) {
        BeanDefine beanDefine = new BeanDefine(bean);
        addBean(name, beanDefine);
    }

    private void addBean(String name, BeanDefine beanDefine) {
        beanPool.put(name, beanDefine);
    }

    @Override
    public Object getBean(String name) throws IllegalAccessException {
        if (StringUtils.isBlank(name)) {
            throw new IllegalAccessException("根据name获得bean name为空!");
        }
        return beanPool.get(name);
    }

    @Override
    public <T> T getBean(Class<T> clazz) throws IllegalAccessException {
        if (Objects.isNull(clazz)) {
            throw new IllegalAccessException("根据class获得bean class为空!");
        }
        Object bean = beanPool.get(clazz.getName());
        if (Objects.isNull(bean)) {
            throw new IllegalAccessException("根据class=" + clazz.getName() + "获取对应bean为空!");
        }
        return clazz.cast(beanPool.get(clazz.getName()));
    }

    @Override
    public Map<String, BeanDefine> getAllBeans() {

        return beanPool;
    }


}
