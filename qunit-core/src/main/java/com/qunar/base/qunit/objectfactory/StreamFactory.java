package com.qunar.base.qunit.objectfactory;

import java.io.InputStream;
import java.lang.reflect.Type;

/**
 * User: zhaohuiyu
 * Date: 6/7/13
 * Time: 6:21 PM
 */
public class StreamFactory extends InstanceFactory {
    @Override
    protected Object create(Type type, Object value) {
        return value;
    }

    @Override
    protected boolean support(Type type) {
        return type.equals(InputStream.class);
    }
}
