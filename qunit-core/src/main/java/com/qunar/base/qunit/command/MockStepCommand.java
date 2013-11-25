/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.command;

import com.qunar.autotest.mock.model.Callback;
import com.qunar.autotest.mock.model.Expectation;
import com.qunar.autotest.mock.model.HttpExpectation;
import com.qunar.autotest.mock.model.RpcExpectation;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.model.MockInfo;
import com.qunar.base.qunit.response.Response;
import com.qunar.base.qunit.util.CloneUtil;
import com.qunar.base.qunit.util.PropertyUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.qunar.autotest.mock.Mocks.whenCall;
import static java.util.Arrays.asList;


/**
 * 描述：
 * Created by JarnTang at 12-6-26 下午6:02
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class MockStepCommand extends ParameterizedCommand {

    private MockInfo mockInfo;
    private Expectation expectation;

    public MockStepCommand(List<KeyValueStore> params) {
        super(params);
    }

    @Override
    protected Response doExecuteInternal(Response preResult, List<KeyValueStore> processedParams, Context context) throws Throwable {
        if (processedParams == null) {
            return preResult;
        }
        mockInfo = new MockInfo(processedParams);
        expectation = mockInfo.createExpectation();
        whenCall(mockInfo.getService())
                .withProductNo(mockInfo.getTarget())
                .withSource(mockInfo.getSource())
                .withKey(mockInfo.getKey())
                .withMockServer(PropertyUtils.getProperty("mockserver", "l-qtest25.tc.beta.cn6.qunar.com"))
                .thenReturn(expectation);
        return preResult;
    }

    @Override
    public StepCommand doClone() {
        return new MockStepCommand(CloneUtil.cloneKeyValueStore(params));
    }

    @Override
    public Map<String, Object> toReport() {
        Map<String, Object> details = new HashMap<String, Object>();
        details.put("stepName", "设置期望:");
        details.put("name", String.format("当来自%s的应用调用业务线%s下的接口%s,并且标识是%s返回如下数据",
                mockInfo.getSource(), mockInfo.getTarget(), mockInfo.getService(), mockInfo.getKey()));
        details.put("params", getReturnValues(expectation));
        return details;
    }

    private List<KeyValueStore> getReturnValues(Expectation expectation) {
        if (expectation == null) return null;
        if (expectation instanceof RpcExpectation) {
            return asList(new KeyValueStore("return", expectation.getBody()));
        } else {
            List<KeyValueStore> params = new ArrayList<KeyValueStore>();
            params.add(new KeyValueStore("return", expectation.getBody()));
            Callback callback = ((HttpExpectation) expectation).getCallback();
            if (callback != null) {
                params.add(new KeyValueStore("callback_url", callback.getUrl()));
                params.add(new KeyValueStore("callback_body", callback.getBody()));
            }
            return params;
        }
    }

}
