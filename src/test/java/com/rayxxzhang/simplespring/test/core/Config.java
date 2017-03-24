package com.rayxxzhang.simplespring.test.core;

import com.rayxxzhang.simplespring.core.annotations.Bean;
import com.rayxxzhang.simplespring.core.annotations.ComponentScan;
import com.rayxxzhang.simplespring.core.annotations.Configuration;

import java.io.PrintStream;

/**
 * Created by Ray on 3/23.
 */
@Configuration
//@ComponentScan(basePackages = {"com.rayxxzhang.simplespring.test"})
@ComponentScan(basePackageClasses = TestBean2.class)
//@ComponentScan
public class Config {
    @Bean
    public TestBean3 testBean3(PrintStream ps, TestBean2 tb2) {
        return new TestBean3(ps, tb2);
    }
    @Bean
    public  PrintStream printStream() {
        return System.out;
    }

}
