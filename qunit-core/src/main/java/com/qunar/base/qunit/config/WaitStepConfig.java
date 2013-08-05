/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.config;

import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.command.WaitStepCommand;

/**
 * 描述：
 * Created by JarnTang at 12-6-5 上午12:33
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class WaitStepConfig extends StepConfig {

    @Property(value = "time", required = true)
    String waitTime;

    @Override
    public StepCommand createCommand() {
        return new WaitStepCommand(Long.valueOf(waitTime));
    }

}
