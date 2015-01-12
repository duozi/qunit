package com.qunar.base.qunit.command;

import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.response.Response;
import com.qunar.base.qunit.util.PropertyUtils;

import java.util.Collections;
import java.util.Map;

/**
 * User: zong.huang
 * Date: 15-1-9
 */
public class CoberturaCommand extends StepCommand {

    private String caseId;

    public CoberturaCommand(String caseId) {
        this.caseId = caseId;
    }

    @Override
    public Response doExecute(Response param, Context context) throws Throwable {
        String cobertura = PropertyUtils.getProperty("cobertura");
        if (Boolean.parseBoolean(cobertura)) {
            logger.info("collect {} cobertura", caseId);
            Runtime.getRuntime().exec(String.format("bash /home/q/tools/devbin/cobertools/cobertura_collect_onecase.sh %s", caseId));
        }
        return null;
    }

    @Override
    protected StepCommand doClone() {
        return new CoberturaCommand(caseId);
    }

    @Override
    public Map<String, Object> toReport() {
        return Collections.emptyMap();
    }
}
