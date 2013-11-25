package com.qunar.base.qunit.command;

import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;
import com.qunar.base.qunit.transport.http.HttpService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: zhaohuiyu
 * Date: 3/14/13
 * Time: 2:38 PM
 */
public class RemoveHeaderCommand extends StepCommand {

    private String header;

    public RemoveHeaderCommand(String header) {
        this.header = header;
    }

    @Override
    public Response doExecute(Response param, Context context) throws Throwable {
        HttpService.removeHeader(header);
        return param;
    }

    @Override
    protected StepCommand doClone() {
        return new RemoveHeaderCommand(header);
    }

    @Override
    public Map<String, Object> toReport() {
        Map<String, Object> details = new HashMap<String, Object>();
        details.put("stepName", "删除指定HTTP头:");
        List<KeyValueStore> params = new ArrayList<KeyValueStore>();
        params.add(new KeyValueStore("header", header));
        details.put("params", params);
        return details;
    }
}
