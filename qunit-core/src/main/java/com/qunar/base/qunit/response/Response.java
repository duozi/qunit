/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.qunar.base.qunit.fastjson.QunitDoubleSerializer;
import com.qunar.base.qunit.util.PropertyUtils;
import com.qunar.base.validator.JsonValidator;
import org.apache.commons.lang.StringUtils;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.DifferenceListener;
import org.custommonkey.xmlunit.XMLUnit;
import org.hamcrest.Matchers;
import org.hamcrest.core.Is;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * 接口调用结果
 * <p/>
 * Created by JarnTang at 12-5-19 下午6:41
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class Response {
    private final static Logger logger = LoggerFactory.getLogger(Response.class);

    Object body;
    Throwable exception;

    public Response() {
    }

    public Response(Object body, Throwable exception) {
        this.exception = exception;
        this.body = body;
    }

    public void verify(Map<String, String> expected) {
        assertBody(expected.get("body"));
        assertException(expected);
        assertXml(expected.get("xml"));
    }

    private void assertXml(String expected) {
        if (StringUtils.isNotBlank(expected)) {
            try {
                XMLUnit.setIgnoreWhitespace(true);
                XMLUnit.setIgnoreAttributeOrder(true);
                XMLUnit.setIgnoreComments(true);
                XMLUnit.setIgnoreDiffBetweenTextAndCDATA(true);
                Diff diff = new Diff(expected, this.body.toString());
                List<String> differents = new ArrayList<String>();
                DifferenceListener differentListener = new IgnoreUndefinedNodeDiffListener(differents);
                diff.overrideDifferenceListener(differentListener);
                boolean similar = diff.similar();
                assertTrue("Xml逻辑是不相等：" + differents.toString(), similar);
            } catch (SAXException e) {
                logger.error(e.getMessage(), e);
                throw new AssertionError(e);
            } catch (IOException e) {
                logger.error(e.getMessage(), e);
                throw new AssertionError(e);
            }
        }
    }

    private void assertBody(String expected) {
        if (expected != null) {
            String json = bodyInString(body);
            assertString(json, expected);
            JsonValidator.validate(expected, json);
        }
    }

    private void assertString(String actual, String expected) {
        if (!isJson(actual)) {
            try {
                assertThat("接口返回的是字符串(非json)，使用字符串对比失败",
                        StringUtils.trim(actual),
                        Is.is(StringUtils.trim(expected)));
            } catch (AssertionError e) {
                assertThat("接口返回的是字符串(非json)，使用字符串对比失败",
                        String.format("\"%s\"", StringUtils.trim(actual)),
                        Is.is(StringUtils.trim(expected)));
            }
        }
    }

    private boolean isJson(String json) {
        try {
            JSON.parse(json);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String bodyInString(Object body) {
        if (body instanceof String)
            return body.toString();
        return responseJson(body);
    }

    private String responseJson(Object body) {
        Boolean jsonWriteOriginalDoubleValue = Boolean.valueOf(PropertyUtils.getProperty("json_write_original_double_value", "false"));
        SerializeConfig config = new SerializeConfig();
        if (jsonWriteOriginalDoubleValue) {
            config.setAsmEnable(false);
            config.put(Double.class, QunitDoubleSerializer.INSTANCE);
        }
        return JSON.toJSONString(body, config, SerializerFeature.WriteMapNullValue);
    }

    private void assertException(Map<String, String> expected) {
        String exceptionClassName = expected.get("class");
        String exceptionMessage = expected.get("message");

        String actualMessage = "";
        if (exception != null) {
            actualMessage = exception.getMessage();
        }
        if (StringUtils.isNotBlank(exceptionClassName)) {
            Class<?> expectedException = getClass(exceptionClassName);
            assertThat("实际抛出的异常的类型和期望的异常类型不同", exception, Matchers.instanceOf(expectedException));
        }

        if (StringUtils.isNotBlank(exceptionMessage)) {
            assertThat("实际抛出异常的message和期望的异常message不同", actualMessage, containsString(exceptionMessage));
        }
    }

    private Class<?> getClass(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            logger.error(String.format("class <%s> not found.", className), e);
            throw new AssertionError(e);
        }
    }

    public void setBody(Object body) {
        this.body = body;
    }

    public Object getBody() {
        return this.body;
    }

    public void setException(Throwable exception) {
        this.exception = exception;
    }

    public Throwable getException() {
        return exception;
    }

    @Override
    public String toString() {
        return "Response{" + "body='" + body + '\'' + ", exception=" + exception + '}';
    }
}
