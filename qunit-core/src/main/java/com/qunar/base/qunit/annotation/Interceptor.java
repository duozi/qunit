package com.qunar.base.qunit.annotation;

import com.qunar.base.qunit.intercept.StepCommandInterceptor;

import java.lang.annotation.ElementType;

/**
 * User: zhaohuiyu
 * Date: 7/17/12
 * Time: 3:54 PM
 */
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.TYPE})
@java.lang.annotation.Inherited
public @interface Interceptor {
    Class<? extends StepCommandInterceptor>[] value();
}
