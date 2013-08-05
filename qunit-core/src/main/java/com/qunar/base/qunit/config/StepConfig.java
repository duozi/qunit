/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.config;

import com.qunar.base.qunit.command.StepCommand;

/**
 * 描述：
 * Created by JarnTang at 12-6-4 下午6:25
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public abstract class StepConfig {

    /*
    This is tricky
     */
    protected String commandName;

    public abstract StepCommand createCommand();
}
