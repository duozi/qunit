/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.command;

import com.alibaba.fastjson.JSON;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 输出上一个command结果信息的command配置
 * <p/>
 * Created by JarnTang at 12-7-18 下午1:15
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class PrintStepCommand extends StepCommand {

    String result;

    @Override
    public Response doExecute(Response param, Context context) throws Throwable {
        if (isOnJenkins()) return param;
        result = JSON.toJSONString(param);
        logger.info(result);
        return param;
    }

    private boolean isOnJenkins() {
        String jenkinsHome = System.getProperty("JENKINS_HOME");
        return StringUtils.isNotBlank(jenkinsHome);
    }

    @Override
    public StepCommand doClone() {
        return new PrintStepCommand();
    }

    @Override
    public Map<String, Object> toReport() {
        Map<String, Object> details = new HashMap<String, Object>();
        details.put("stepName", "打印上一步执行器的结果信息");
        details.put("name", result);
        List<KeyValueStore> params = new ArrayList<KeyValueStore>();
        details.put("params", params);
        return details;
    }

}
