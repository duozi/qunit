package com.qunar.base.qunit.transport.config;

import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.transport.command.ExecuteCommand;
import com.qunar.base.qunit.transport.command.LocalExecuteCommand;

public class LocalServiceConfig extends ServiceConfig {
    public final static String name = "local";

    @Property("class")
    private String className;

    @Property
    private String method;

    @Override
    public ExecuteCommand createCommand() {
        return new LocalExecuteCommand(this.id, this.className, this.method, this.desc);
    }
}
