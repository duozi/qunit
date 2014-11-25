package com.qunar.base.qunit.config;

import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.command.WaitUntilStepCommand;

/**
 * User: zhaohuiyu
 * Date: 6/12/12
 * Time: 11:37 AM
 */
public class WaitUntilStepConfig extends CompositeStepConfig {

    @Property(required = true)
    private String timeout;

    @Property
    private String desc;

    @Override
    public StepCommand createCommand() {
        return new WaitUntilStepCommand(desc, Long.valueOf(timeout), this.createChildren());
    }
}
