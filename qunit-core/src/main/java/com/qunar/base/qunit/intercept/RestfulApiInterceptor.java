package com.qunar.base.qunit.intercept;

import com.qunar.base.qunit.command.CallStepCommand;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;
import com.qunar.base.qunit.transport.command.HttpExecuteCommand;
import com.qunar.base.qunit.util.ParameterUtils;
import com.qunar.base.qunit.util.ReflectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: zhaohuiyu
 * Date: 7/13/12
 * Time: 6:33 PM
 */
/*
For some restful api, there are parameters in url, such as:
http://www.qunar.com/blog/zhaohui/2012/07/13/1.html
Since we want to treat zhaohui, 2012, 07, 13, 1 as parameters, so we create this interceptor to deal with it.
 */
public class RestfulApiInterceptor implements StepCommandInterceptor {

    @Override
    public Object beforeExecute(StepCommand command, Response preResult, Context context) {
        if (!(command instanceof CallStepCommand)) return preResult;

        Object executeCommand = ReflectionUtils.getValue(command, "command");
        if (!(executeCommand instanceof HttpExecuteCommand)) return preResult;

        String url = (String) ReflectionUtils.getValue(executeCommand, "url");
        List<String> parametersInUrl = extractParameters(url);
        if (parametersInUrl.isEmpty()) return preResult;

        List<KeyValueStore> commandParameters = (List<KeyValueStore>) ReflectionUtils.getValue(command, "params");
        commandParameters = ParameterUtils.prepareParameters(commandParameters, preResult, context);
        url = replace(url, commandParameters, parametersInUrl);

        commandParameters = removeUsedParameters(commandParameters, parametersInUrl);
        executeCommand = clone(executeCommand);
        ReflectionUtils.setFieldValue(executeCommand, "url", url);
        ReflectionUtils.setFieldValue(command, "params", commandParameters);
        ReflectionUtils.setFieldValue(command, "command", executeCommand);

        return preResult;
    }

    //ExecuteCommand(HttpExecuteCommand, RpcExecuteCommand, LocalExecuteCommand) is shared for all test cases,
    //if we want to modify its properties, we should clone it.
    private Object clone(Object executeCommand) {
        return new HttpExecuteCommand(
                (String) ReflectionUtils.getValue(executeCommand, "id"), "",
                (String) ReflectionUtils.getValue(executeCommand, "method"),
                (String) ReflectionUtils.getValue(executeCommand, "desc"));
    }

    private List<KeyValueStore> removeUsedParameters(List<KeyValueStore> params, List<String> parameterNames) {
        List<KeyValueStore> newParams = new ArrayList<KeyValueStore>(params);
        for (String key : parameterNames) {
            remove(newParams, key);
        }
        return newParams;
    }

    private void remove(List<KeyValueStore> params, String key) {
        Iterator<KeyValueStore> iterator = params.iterator();
        while (iterator.hasNext()) {
            KeyValueStore keyValueStore = iterator.next();
            if (keyValueStore.getName().equals(key)) {
                iterator.remove();
            }
        }
    }

    private String replace(String url, List<KeyValueStore> params, List<String> parameterNames) {
        for (String name : parameterNames) {
            String value = get(params, name);
            url = url.replace(String.format("{%s}", name), value);
        }
        return url;
    }


    private String get(List<KeyValueStore> params, String key) {
        for (KeyValueStore param : params) {
            if (param.getName().equals(key)) return (String) param.getValue();
        }
        return StringUtils.EMPTY;
    }

    //http://www.qunar.com/blog/{bloger}/{year}/{month}/{day}/{id}.json
    private final static Pattern pattern = Pattern.compile("\\{([A-Za-z]+)\\}");

    protected static List<String> extractParameters(String url) {
        Matcher matcher = pattern.matcher(url);
        ArrayList<String> result = new ArrayList<String>();
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }

    @Override
    public Object afterExecute(StepCommand command, Response response, Context context) {
        return response;
    }
}
