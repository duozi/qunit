/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.command;

import com.qunar.autotest.mock.util.JSON;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.exception.ExecuteException;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.qunar.base.qunit.util.CloneUtil.cloneKeyValueStore;

/**
 * 描述：
 * Created by JarnTang at 12-6-4 下午6:10
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class AssertStepCommand extends ParameterizedCommand {
    private final static String separator = System.getProperty("line.separator", "\r\n");

    private String error = StringUtils.EMPTY;

    public AssertStepCommand(List<KeyValueStore> params) {
        super(params);
    }

    @Override
    protected Response doExecuteInternal(Response preResult, List<KeyValueStore> processedParams, Context context) throws Throwable {
        Map<String, String> expectation = convertKeyValueStoreToMap(processedParams);
        try {
            logger.info("assert command<{}> is starting... ", expectation);
            preResult.verify(expectation);
            return preResult;
        } catch (Exception e) {
            String message = "assert step invoke has error,expect=" + expectation + separator + "result=" + preResult;
            logger.error(message, e);
            error = JSON.toJSONString(preResult);
            throw new ExecuteException(message, e);
        }
    }

    @Override
    public StepCommand doClone() {
        return new AssertStepCommand(cloneKeyValueStore(this.params));
    }

    @Override
    public Map<String, Object> toReport() {
        Map<String, Object> details = new HashMap<String, Object>();
        details.put("stepName", "验证：");
        List<KeyValueStore> params = new ArrayList<KeyValueStore>();
        params.addAll(this.params);
        //if (StringUtils.isNotBlank(error)) {
            params.add(new KeyValueStore("实际值", error));
        //}
        details.put("params", params);
        return details;
    }

    private Map<String, String> convertKeyValueStoreToMap(List<KeyValueStore> params) {
        Map<String, String> result = new HashMap<String, String>();
        for (KeyValueStore kvs : params) {
            Object value = kvs.getValue();
            if (value instanceof Map) {
                result.putAll((Map) value);
            } else {
                result.put(kvs.getName(), (String) value);
            }
        }
        return result;
    }

}
