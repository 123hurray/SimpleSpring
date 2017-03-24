package com.rayxxzhang.simplespring.test.core;

import com.rayxxzhang.simplespring.core.AnnotationConfigApplicationContext;
import com.rayxxzhang.simplespring.core.ApplicationContext;
import org.junit.Test;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;

/**
 * Created by Ray on 3/23.
 */
public class SimpleSpringTests {
    @Test
    public void testApplicationContext() throws IOException, ClassNotFoundException, URISyntaxException, IllegalAccessException, InvocationTargetException, InstantiationException {
        ApplicationContext ctx = new AnnotationConfigApplicationContext(Config.class);
        TestBean tb = ctx.getBean(TestBean.class);
        tb.getTb2().getTb3().getTestBean2().getTb3().testMethod3();
    }
}
