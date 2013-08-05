/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.annotation;

import java.lang.annotation.ElementType;

/**
 * 参数注解
 *
 * Created by JarnTang at 12-6-3 下午5:54
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.FIELD})
@java.lang.annotation.Inherited
public @interface Element {

    public String value() default "param";

}
