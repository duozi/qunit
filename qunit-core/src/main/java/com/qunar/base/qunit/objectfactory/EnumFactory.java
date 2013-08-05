package com.qunar.base.qunit.objectfactory;

import com.qunar.base.qunit.util.Util;

import java.lang.reflect.Type;

/**
 * User: zhaohuiyu
 * Date: 8/22/12
 * Time: 12:34 PM
 */
public class EnumFactory extends InstanceFactory {
    @Override
    protected Object create(Type type, Object value) {
        if (Util.isEmpty(value)) return null;

        return Enum.valueOf((Class) type, value.toString());
    }

    @Override
    protected boolean support(Type type) {
        if (type instanceof Class) {
            return ((Class) type).isEnum();
        }
        return false;
    }
}
