package com.qunar.base.qunit.model;

import com.qunar.autotest.mock.model.Expectation;
import com.qunar.autotest.mock.model.HttpExpectation;
import com.qunar.autotest.mock.model.RpcExpectation;
import com.qunar.base.qunit.objectfactory.BeanUtils;
import org.apache.commons.lang.StringUtils;

import java.util.List;

import static java.util.Arrays.asList;

public class MockInfo {
    public static final String SOURCE = "source";
    public static final String TARGET = "target";
    public static final String KEY = "key";
    public static final String RETURN_VALUE = "returnValue";
    public static final String SERVICE = "service";

    private List<KeyValueStore> params;

    public MockInfo(List<KeyValueStore> params) {
        this.params = params;
    }

    public Expectation createExpectation() {
        String body = getBody();
        if (isRpc()) {
            RpcExpectation expectation = new RpcExpectation();
            expectation.setBody(body);
            return expectation;
        } else {
            if (StringUtils.isNotBlank(body)) {
                HttpExpectation expectation = new HttpExpectation();
                expectation.setBody(body);
                return expectation;
            }
            Object expectation = get("return");
            Object[] parameters = BeanUtils.getParameters(asList(new KeyValueStore("return", expectation)), new Class[]{HttpExpectation.class});
            return (Expectation) parameters[0];
        }
    }

    private String getBody() {
        if (StringUtils.isNotBlank(getReturnValue())) {
            return getReturnValue();
        }
        if (params == null || params.size() < 1) {
            return null;
        }
        Object returnValue = get("return");
        if (returnValue instanceof String) return returnValue.toString();
        return null;
    }

    private boolean isRpc() {
        return getService().toLowerCase().startsWith("rpc:");
    }

    public String getService() {
        return get(SERVICE);
    }

    public String getKey() {
        return get(KEY);
    }

    public String getTarget() {
        return get(TARGET);
    }

    public String getSource() {
        return get(SOURCE);
    }

    private <T> T get(String key) {
        for (KeyValueStore param : params) {
            if (param.getName().equalsIgnoreCase(key)) return (T) param.getValue();
        }
        return null;
    }

    public String getReturnValue() {
        return get(RETURN_VALUE);
    }
}

