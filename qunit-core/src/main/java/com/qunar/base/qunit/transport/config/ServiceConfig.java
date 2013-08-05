package com.qunar.base.qunit.transport.config;

import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.transport.command.ExecuteCommand;

public abstract class ServiceConfig {

    @Property
    protected String id;

    @Property
    protected String desc;

    public abstract ExecuteCommand createCommand();
}
