/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.config;

import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.command.LoopStepCommand;
import com.qunar.base.qunit.command.StepCommand;

/**
 * 循环执行器配置信息
 * <p/>
 * Created by JarnTang at 12-7-17 下午12:01
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class LoopStepConfig extends CompositeStepConfig {

    @Property(required = true)
    private String time;

    @Override
    public StepCommand createCommand() {
        return new LoopStepCommand(Integer.valueOf(time), this.createChildren());
    }

}
