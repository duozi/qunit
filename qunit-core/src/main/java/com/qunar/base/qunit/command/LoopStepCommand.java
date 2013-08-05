/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.command;

import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.qunar.base.qunit.util.CloneUtil.cloneStepCommand;

/**
 * 循环执行器，循环执行指定的次数
 * <p/>
 * Created by JarnTang at 12-7-17 上午11:57
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class LoopStepCommand extends CompositeStepCommand {

    private int times;

    public LoopStepCommand(int times, List<StepCommand> children) {
        super(children);
        this.times = times;
    }

    @Override
    public Response doExecute(Response preResult, Context context) throws Throwable {
        Response response = preResult;
        for (long i = 0; i < times; i++) {
            response = super.doExecute(preResult, context);
        }
        return response;
    }

    @Override
    public StepCommand doClone() {
        return new LoopStepCommand(this.times, cloneStepCommand(children));
    }

    @Override
    public Map<String, Object> toReport() {
        Map<String, Object> details = new HashMap<String, Object>();
        details.put("stepName", "执行:");
        details.put("name","循环执行" + String.valueOf(times) + "次command" + getChildren());
        List<KeyValueStore> params = new ArrayList<KeyValueStore>();
        details.put("params", params);
        return details;
    }

}
