package com.qunar.base.qunit.config;

import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.command.RemoveHeaderCommand;
import com.qunar.base.qunit.command.StepCommand;

/**
 * User: zhaohuiyu
 * Date: 3/14/13
 * Time: 2:38 PM
 */
public class RemoveHeaderConfig extends StepConfig {

    @Property(required = true)
    String header;

    @Override
    public StepCommand createCommand() {
        return new RemoveHeaderCommand(header);
    }
}
