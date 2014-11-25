package com.qunar.base.qunit.command;

import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static com.qunar.base.qunit.util.CloneUtil.cloneStepCommand;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;

/**
 * User: zhaohuiyu
 * Date: 6/12/12
 * Time: 11:49 AM
 */
public class WaitUntilStepCommand extends CompositeStepCommand {

    private Long timeout;

    private String desc;

    private Response response;
    private static final Long INTERVAL = 100L;

    public WaitUntilStepCommand(String desc, Long timeout, List<StepCommand> children) {
        super(children);
        this.timeout = timeout;
        this.desc = desc;
    }

    @Override
    public Response doExecute(Response param, Context context) throws Throwable {
        long end = end();
        while (true) {
            Boolean success = executeChildren(param, context);
            if (success) {
                return this.response;
            } else {
                if (isTimeout(end)) {
                    throw new TimeoutException(String.format("超时时间已过: %s", this.timeout));
                }
                waitAWhile();
            }
        }
    }

    private void waitAWhile() throws InterruptedException {
        TimeUnit.MILLISECONDS.sleep(INTERVAL);
    }

    @Override
    public StepCommand doClone() {
        return new WaitUntilStepCommand(desc, timeout, cloneStepCommand(children));
    }

    private boolean isTimeout(long end) {
        return System.currentTimeMillis() > end;
    }

    private Boolean executeChildren(Response param, Context context) {
        try {
            this.response = super.doExecute(param, context);
            return TRUE;
        } catch (Throwable e) {
            return FALSE;
        }
    }

    private long end() {
        return System.currentTimeMillis() + this.timeout;
    }

    @Override
    public Map<String, Object> toReport() {
        Map<String, Object> details = new HashMap<String, Object>();
        details.put("stepName", String.format("等待子命令执行完毕或超时:%s", desc));
        details.put("name", String.valueOf(this.timeout) + "ms");
        List<KeyValueStore> params = new ArrayList<KeyValueStore>();
        details.put("params", params);
        return details;
    }

}
