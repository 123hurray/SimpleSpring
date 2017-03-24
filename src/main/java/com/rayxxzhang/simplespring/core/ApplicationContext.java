package com.rayxxzhang.simplespring.core;

/**
 * Created by Ray on 3/23.
 */
public interface ApplicationContext {
    <T> T getBean(Class<T> clazz);
}
