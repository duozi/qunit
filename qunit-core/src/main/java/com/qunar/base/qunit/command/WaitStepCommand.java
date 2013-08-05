/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.command;

import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.exception.ExecuteException;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 描述：
 * Created by JarnTang at 12-6-4 下午6:02
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class WaitStepCommand extends StepCommand {

    long waitTime;

    public WaitStepCommand(long time) {
        waitTime = time;
    }

    @Override
    public Response doExecute(Response result, Context context) throws Exception {
        try {
            logger.info("wait command<time={}ms> is starting...", waitTime);
            TimeUnit.MILLISECONDS.sleep(waitTime);
            return result;
        } catch (InterruptedException e) {
            String message = String.format("wait step command<time=%sms> execute error", waitTime);
            logger.error(message, e);
            throw new ExecuteException(message, e);
        }
    }

    @Override
    public StepCommand doClone() {
        return new WaitStepCommand(waitTime);
    }

    @Override
    public Map<String, Object> toReport() {
        Map<String, Object> details = new HashMap<String, Object>();
        details.put("stepName", "等待:");
        details.put("name", String.valueOf(this.waitTime) + "ms");
        List<KeyValueStore> params = new ArrayList<KeyValueStore>();
        details.put("params", params);
        return details;
    }

}
