package com.qunar.base.qunit.objectfactory;

import com.qunar.base.qunit.util.Util;

import java.lang.reflect.Type;
import java.math.BigInteger;

/**
 * User: zhaohuiyu
 * Date: 8/22/12
 * Time: 12:24 PM
 */
public class BigIntegerFactory extends InstanceFactory {
    @Override
    protected Object create(Type type, Object value) {
        if (Util.isEmpty(value)) return null;
        return new BigInteger(value.toString());
    }

    @Override
    protected boolean support(Type type) {
        return type.equals(BigInteger.class);
    }
}
