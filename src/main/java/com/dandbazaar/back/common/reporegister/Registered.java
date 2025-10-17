package com.dandbazaar.back.common.reporegister;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface Registered {
    Class<?> value();

    String beanName() default "";

    boolean autoRegister() default true;

    int order() default 0;

    String[] tags() default {};
}
