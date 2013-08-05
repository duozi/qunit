/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.config;

import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.command.TearDownStepCommand;

/**
 * 组装清理数据的Command
 *
 * Created by JarnTang at 12-7-31 下午2:57
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class TearDownStepConfig extends CompositeStepConfig{

    @Override
    public StepCommand createCommand() {
        return new TearDownStepCommand(this.createChildren());
    }

}
