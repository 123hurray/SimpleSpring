package com.rayxxzhang.simplespring.core.annotations;

import java.lang.annotation.*;

/**
 * Created by Ray on 3/23.
 */
@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = ElementType.TYPE)
public @interface ComponentScan {
    String[] basePackages() default {};
    Class<?>[] basePackageClasses() default {};
}
