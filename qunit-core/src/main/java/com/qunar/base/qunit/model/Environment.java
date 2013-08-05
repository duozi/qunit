/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.model;

import com.qunar.base.qunit.Qunit;

import java.util.HashMap;
import java.util.Map;

/**
 * 环境变量
 *
 * Created by JarnTang at 12-6-14 下午3:52
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class Environment {

    public static final String OPERATION = "OPERATION";

    static final Map<String,Object> environments = new HashMap<String, Object>();

    public static void addEnvironment(String key, Object value) {
        environments.put(key, value);
    }

    public static Object getEnvironment(String key) {
        return environments.get(key);
    }

    public static void initEnvironment(Class<?> clazz){
        Qunit.Options options = clazz.getAnnotation(Qunit.Options.class);
        addEnvironment(OPERATION, options.operation().valueOf());
    }

}
