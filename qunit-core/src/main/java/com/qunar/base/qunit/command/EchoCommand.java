package com.qunar.base.qunit.command;

import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: zhaohuiyu
 * Date: 12/18/12
 */
public class EchoCommand extends ParameterizedCommand {
    public EchoCommand(List<KeyValueStore> params) {
        super(params);
    }

    @Override
    protected Response doExecuteInternal(Response preResult, List<KeyValueStore> processedParams, Context context) throws Throwable {
        Response response = new Response();
        response.setBody(processedParams.get(0).getValue());
        return response;
    }

    @Override
    protected StepCommand doClone() {
        return new EchoCommand(this.params);
    }

    @Override
    public Map<String, Object> toReport() {
        Map<String, Object> details = new HashMap<String, Object>();
        details.put("stepName", "echo");
        List<KeyValueStore> params = new ArrayList<KeyValueStore>();
        details.put("params", params);
        return details;
    }
}
