/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.config;

import com.qunar.base.qunit.annotation.Element;
import com.qunar.base.qunit.command.SetStepCommand;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.model.KeyValueStore;

import java.util.List;

/**
 * 设置参数
 *
 * Created by JarnTang at 12-7-24 下午4:48
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class SetStepConfig extends StepConfig{

    @Element
    List<KeyValueStore> parameter;

    @Override
    public StepCommand createCommand() {
        return new SetStepCommand(parameter);
    }

}
