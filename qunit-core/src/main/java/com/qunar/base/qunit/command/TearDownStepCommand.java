/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.command;

import com.qunar.base.qunit.model.KeyValueStore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.qunar.base.qunit.util.CloneUtil.cloneStepCommand;

/**
 * case运行完后清理状态的命令
 *
 * Created by JarnTang at 12-7-31 下午1:37
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class TearDownStepCommand extends CompositeStepCommand {

    public TearDownStepCommand(List<StepCommand> children) {
        super(children);
    }

    @Override
    public StepCommand doClone() {
        return new TearDownStepCommand(cloneStepCommand(children));
    }

    @Override
    public Map<String, Object> toReport() {
        Map<String, Object> details = new HashMap<String, Object>();
        details.put("stepName", "清理工作:");
        details.put("name", "调用Command" + getChildren());
        List<KeyValueStore> params = new ArrayList<KeyValueStore>();
        details.put("params", params);
        return details;
    }

}
