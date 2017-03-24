package com.rayxxzhang.simplespring.core;

import com.rayxxzhang.simplespring.core.annotations.Component;
import com.rayxxzhang.simplespring.core.annotations.ComponentScan;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

/**
 * Created by Ray on 3/23.
 */
@Component(name = "Main")
@ComponentScan(basePackages = {"com.rayxxzhang.simplespring"})
public class Main {
    public static void main(String[] args) throws IOException, ClassNotFoundException, URISyntaxException, IllegalAccessException, InvocationTargetException, InstantiationException {
        new AnnotationConfigApplicationContext(Main.class);
    }
}
