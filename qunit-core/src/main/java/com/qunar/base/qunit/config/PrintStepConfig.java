/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.config;

import com.qunar.base.qunit.command.PrintStepCommand;
import com.qunar.base.qunit.command.StepCommand;

/**
 * 输出上一个command结果信息的command配置
 *
 * Created by JarnTang at 12-7-18 下午1:13
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class PrintStepConfig extends StepConfig {

    @Override
    public StepCommand createCommand() {
        return new PrintStepCommand();
    }

}
