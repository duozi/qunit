/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.config;

import com.qunar.base.qunit.annotation.Element;
import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.command.CallStepCommand;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.transport.command.ExecuteCommand;
import com.qunar.base.qunit.transport.command.HttpExecuteCommand;
import com.qunar.base.qunit.transport.command.ServiceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

/**
 * 描述：
 * Created by JarnTang at 12-6-5 上午12:24
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class CallStepConfig extends StepConfig {

    @Property(required = true)
    String service;

    @Element
    List<KeyValueStore> params;

    @Override
    public StepCommand createCommand() {
        ExecuteCommand command = ServiceFactory.getInstance().getCommand(service);
        return new CallStepCommand(command, params);
    }
}
