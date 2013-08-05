package com.qunar.base.qunit.annotation;

import java.lang.annotation.ElementType;

/**
 * User: zhaohuiyu
 * Date: 6/11/12
 * Time: 4:25 PM
 */
@java.lang.annotation.Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@java.lang.annotation.Target({ElementType.TYPE})
@java.lang.annotation.Inherited
public @interface ConfigElement {
    String defaultProperty();
}
