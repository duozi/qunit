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
public abstract class StepConfig implements Cloneable{

    /*
    This is tricky
     */
    protected String commandName;

    public abstract StepCommand createCommand();

    public Object clone(){
        StepConfig sc = null;
        try {
            sc = (StepConfig)super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();  //To change body of catch statement use File | Settings | File Templates.
        }
        return sc;
    }
    //protected abstract StepConfig doClone();

    public String getCommandName() {
        return commandName;
    }
}
