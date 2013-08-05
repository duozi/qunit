package com.qunar.base.qunit.objectfactory;

import java.lang.reflect.Type;

/**
 * User: zhaohuiyu
 * Date: 10/9/12
 */
public class CharFactory extends InstanceFactory {
    @Override
    protected Object create(Type type, Object value) {
        if (value == null) {
            return type.equals(Character.TYPE) ? ' ' : null;
        }
        String s = value.toString();
        return s.charAt(0);
    }

    @Override
    protected boolean support(Type type) {
        return type.equals(Character.class) || type.equals(Character.TYPE);
    }
}
