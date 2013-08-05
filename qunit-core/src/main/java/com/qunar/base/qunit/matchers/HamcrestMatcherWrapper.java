package com.qunar.base.qunit.matchers;

import com.qunar.base.qunit.util.ReflectionUtils;
import org.hamcrest.Matcher;
import org.hamcrest.Matchers;

import java.lang.reflect.Method;

/**
 * User: zhaohuiyu
 * Date: 6/8/12
 * Time: 10:29 AM
 */
public class HamcrestMatcherWrapper {
    private String methodName;

    public HamcrestMatcherWrapper(String methodName) {
        this.methodName = methodName;
    }

    public Matcher matches(Object expected){
        Method method = ReflectionUtils.getMethod(Matchers.class, this.methodName, expected.getClass());
        return (Matcher) ReflectionUtils.invoke(method, null, expected);
    }

}
