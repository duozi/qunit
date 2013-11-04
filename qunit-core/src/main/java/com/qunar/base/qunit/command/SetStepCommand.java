package com.qunar.base.qunit.command;

import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.qunar.base.qunit.util.CloneUtil.cloneKeyValueStore;

public class SetStepCommand extends ParameterizedCommand {

    public SetStepCommand(List<KeyValueStore> params) {
        super(params);
    }

    @Override
    protected Response doExecuteInternal(Response preResult, List<KeyValueStore> processedParams, Context context) {
        logger.info("set command<{}> is staring ...", getParamsAsString(processedParams));
        context.addContext(convertToMap(processedParams));
        return preResult;
    }

    @Override
    public StepCommand doClone() {
        return new SetStepCommand(cloneKeyValueStore(params));
    }

    @Override
    public Map<String, Object> toReport() {
        Map<String, Object> details = new HashMap<String, Object>();
        details.put("stepName", "设置Case环境变量");
        details.put("params", params);
        return details;
    }

    private Map<String, Object> convertToMap(List<KeyValueStore> processedParams) {
        Map<String, Object> result = new HashMap<String, Object>();
        for (KeyValueStore kvs : processedParams) {
            result.put(kvs.getName(), kvs.getValue());
        }
        return result;
    }

    private String getParamsAsString(List<KeyValueStore> params) {
        if (params == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (KeyValueStore kv : params) {
            sb.append(kv.getName()).append("=").append(kv.getValue()).append("&");
        }

        return sb.toString();
    }


}
