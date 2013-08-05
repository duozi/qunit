package com.qunar.base.qunit.objectfactory;

import java.lang.reflect.Type;

public class IntFactory extends InstanceFactory {
    @Override
    protected Object create(Type type, Object value) {
        if (value == null) {
            return type.equals(Integer.TYPE) ? 0 : null;
        }
        return Integer.valueOf(value.toString());
    }

    @Override
    protected boolean support(Type type) {
        return type.equals(Integer.class) || type.equals(Integer.TYPE);
    }
}
