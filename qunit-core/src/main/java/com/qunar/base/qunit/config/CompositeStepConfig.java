package com.qunar.base.qunit.config;

import com.qunar.base.qunit.annotation.ChildrenConfig;
import com.qunar.base.qunit.command.StepCommand;

import java.util.ArrayList;
import java.util.List;

/**
 * User: zhaohuiyu
 * Date: 6/12/12
 * Time: 10:42 AM
 */
public abstract class CompositeStepConfig extends StepConfig {
    @ChildrenConfig
    private List<StepConfig> childrenConfig;

    protected final List<StepCommand> createChildren() {
        List<StepCommand> commands = new ArrayList<StepCommand>();
        for (StepConfig config : childrenConfig) {
            StepCommand command = config.createCommand();
            commands.add(command);
        }
        return commands;
    }
}
