package com.qunar.base.qunit.intercept;

import com.qunar.base.qunit.command.ParameterizedCommand;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;
import com.qunar.base.qunit.util.ReflectionUtils;

import java.util.List;

import static com.qunar.base.qunit.util.ParameterUtils.prepareParameters;

/**
 * User: zhaohuiyu
 * Date: 7/17/12
 * Time: 4:34 PM
 */
public abstract class ParameterInterceptor implements StepCommandInterceptor {

    @Override
    public Object beforeExecute(StepCommand command, Response preResult, Context context) {
        if (!(command instanceof ParameterizedCommand)) return preResult;
        List<KeyValueStore> params = (List<KeyValueStore>) ReflectionUtils.getValue(command, "params");
        if (!support(params)) return preResult;
        List<KeyValueStore> processedParams = prepareParameters(params, preResult, context);
        processedParams = convert(processedParams);
        ReflectionUtils.setFieldValue(command, "params", processedParams);
        return preResult;
    }

    protected abstract List<KeyValueStore> convert(List<KeyValueStore> params);

    protected abstract boolean support(List<KeyValueStore> params);

    @Override
    public Object afterExecute(StepCommand command, Response response, Context context) {
        return response;
    }
}
