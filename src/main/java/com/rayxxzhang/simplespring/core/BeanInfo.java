package com.rayxxzhang.simplespring.core;

import java.lang.reflect.Method;

/**
 * Created by Ray on 3/24.
 */
public class BeanInfo {
    private Object config;
    private Method method;
    private String name;

    public BeanInfo(Object config, Method method) {
        this.config = config;
        this.method = method;
        this.name = null;
    }
    public BeanInfo(Object config, Method method, String name) {
        this.config = config;
        this.method = method;
        this.name = name;
    }

    public Object getConfig() {
        return config;
    }

    public Method getMethod() {
        return method;
    }

    public String getName() {
        return name;
    }
}
