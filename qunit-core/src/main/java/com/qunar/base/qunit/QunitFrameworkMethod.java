/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit;

import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.TestCase;
import org.apache.commons.lang.StringUtils;
import org.junit.runners.model.FrameworkMethod;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

/**
 * 描述：
 * Created by JarnTang at 12-5-19 下午2:22
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class QunitFrameworkMethod extends FrameworkMethod {

    private TestCase testCase;
    private Context context;

    public QunitFrameworkMethod(Method method, TestCase testCase, Context context) {
        super(method);
        this.testCase = testCase;
        this.context = context;
    }

    @Override
    public String getName() {
        return getName(this.testCase);
    }

    private String getName(TestCase testCase) {
        if (StringUtils.isNotBlank(testCase.getDesc())) {
            return testCase.getDesc();
        }
        return testCase.getId();
    }

    @Override
    public Annotation[] getAnnotations() {
        return new Annotation[0];
    }

    public <T extends Annotation> T getAnnotation(Class<T> annotationType) {
        return null;
    }

    public TestCase getTestCase() {
        return testCase;
    }

    public Context getContext() {
        return context;
    }
}
