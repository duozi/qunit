package com.qunar.base.qunit.command;

import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.exception.CommandNotFoundException;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: xiaofen.zhang
 * Date: 12-6-7
 * Time: 下午10:31
 */
public class TransformStepCommand extends StepCommand {
    Transform ts;

    public TransformStepCommand(Transform ts) {
        this.ts = ts;
    }

    @Override
    public Response doExecute(Response result, Context context) throws Throwable {
        if (ts == null) {
            throw new CommandNotFoundException("transform command not founded.");
        }
        if (result == null) {
            result = new Response();
            result.setBody(ts.transport(null));
        } else {
            result.setBody(ts.transport(result.getBody()));
        }
        return result;
    }

    @Override
    public StepCommand doClone() {
        return new TransformStepCommand(ts);
    }

    @Override
    public Map<String, Object> toReport() {
        Map<String, Object> details = new HashMap<String, Object>();
        details.put("stepName", "执行转换器:");
        details.put("name", ts.getClass().getCanonicalName());
        List<KeyValueStore> params = new ArrayList<KeyValueStore>();
        details.put("params", params);
        return details;
    }
}
