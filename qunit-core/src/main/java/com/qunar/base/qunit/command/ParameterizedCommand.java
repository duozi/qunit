package com.qunar.base.qunit.command;

import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;

import java.util.List;

import static com.qunar.base.qunit.util.ParameterUtils.prepareParameters;

/**
 * User: zhaohuiyu
 * Date: 6/26/12
 * Time: 11:16 AM
 */
public abstract class ParameterizedCommand extends StepCommand {

    protected List<KeyValueStore> params;

    public ParameterizedCommand(List<KeyValueStore> params) {
        this.params = params;
    }

    @Override
    public Response doExecute(Response preResult, Context context) throws Throwable {
        List<KeyValueStore> processedParameters = prepareParameters(this.params, preResult, context);
        return doExecuteInternal(preResult, processedParameters, context);
    }

    protected abstract Response doExecuteInternal(Response preResult, List<KeyValueStore> processedParams,
                                                  Context context) throws Throwable;

    public List<KeyValueStore> getParams() {
        return params;
    }

}
