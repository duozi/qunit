package com.qunar.base.qunit.annotation;

import java.lang.annotation.ElementType;

@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.FIELD})
@java.lang.annotation.Inherited
public @interface Property {
    public String value() default "";

    public String defaultValue() default "";

    public boolean required() default false;
}
