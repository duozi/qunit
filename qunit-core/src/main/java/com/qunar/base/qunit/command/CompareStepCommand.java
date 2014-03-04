package com.qunar.base.qunit.command;

import com.qunar.autotest.mock.util.JSON;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.exception.ExecuteException;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;
import com.qunar.base.qunit.util.Util;
import org.apache.commons.lang.StringUtils;

import java.util.*;

import static com.qunar.base.qunit.util.CloneUtil.cloneKeyValueStore;

/**
 * User: zonghuang
 * Date: 2/28/14
 */
public class CompareStepCommand extends ParameterizedCommand{

    private final static String separator = System.getProperty("line.separator", "\r\n");

    private String error = StringUtils.EMPTY;

    private String ignore;

    public CompareStepCommand(List<KeyValueStore> params, String ignore) {
        super(params);
        this.ignore = ignore;
    }

    @Override
    protected Response doExecuteInternal(Response preResult, List<KeyValueStore> processedParams, Context context) throws Throwable {
        Map<String, String> expectation = convertKeyValueStoreToMap(processedParams);
        logger.info("assert command<{}> is starting... ", expectation);
        Object body = preResult.getBody();
        Map<String, String> actual = null;
        Map<String, String> expect = null;
        if (Util.isJson(expectation.get("body"))) {
            expect = (Map<String, String>) com.alibaba.fastjson.JSON.parse(expectation.get("body"));
        }
        if (Util.isJson(body)) {
            actual = (Map<String, String>) com.alibaba.fastjson.JSON.parse((String) body);
        }
        if (StringUtils.isNotBlank(ignore) && actual != null && expect != null) {
            processIgnores(actual, expect);
        }
        expectation.put("body", com.alibaba.fastjson.JSON.toJSONString(expect));
        try {
            preResult.verify(expectation);
            return preResult;
        } catch (Exception e) {
            String message = "assert step invoke has error,expect=" + expectation + separator + "result=" + preResult;
            logger.error(message, e);
            error = JSON.toJSONString(preResult);
            throw new ExecuteException(message, e);
        }
    }

    private void processIgnores(Map<String, String> body, Map<String, String> expection) {
        List<String> ignoreList = Arrays.asList(StringUtils.split(ignore, ","));
        for (String ignore : ignoreList) {
            processIgnore(body, ignore);
            processIgnore(expection, ignore);
        }

    }

    private void processIgnore(Map<String, String> map, String ignore) {
        if (map == null || StringUtils.isBlank(ignore)) return;
        String[] array = StringUtils.split(ignore, ".");
        int i = 0;
        int count = array.length;
        traversalMap(map, array, i, count);
    }

    private void traversalMap(Map<String, String> map, String[] array, int index, int count) {
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (entry.getKey().equals(array[index])) {
                if (index < count - 1) {
                    index++;
                } else if (index == count - 1) {
                    iterator.remove();
                }
            }
            if (entry.getValue() instanceof Map) {
                traversalMap((Map<String, String>) entry.getValue(), array, index, count);
            }
        }
    }

    @Override
    protected StepCommand doClone() {
        return new CompareStepCommand(cloneKeyValueStore(this.params), this.ignore);
    }

    @Override
    public Map<String, Object> toReport() {
        Map<String, Object> details = new HashMap<String, Object>();
        details.put("stepName", "验证:");
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
