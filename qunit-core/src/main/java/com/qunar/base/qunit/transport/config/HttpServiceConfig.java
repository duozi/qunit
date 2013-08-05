package com.qunar.base.qunit.transport.config;

import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.transport.command.ExecuteCommand;
import com.qunar.base.qunit.transport.command.HttpExecuteCommand;

public class HttpServiceConfig extends ServiceConfig {
    public final static String name = "http";

    @Property
    protected String url;

    @Property(defaultValue = "post")
    protected String method;

    @Override
    public ExecuteCommand createCommand() {
        return new HttpExecuteCommand(this.id, this.url, this.method, this.desc);
    }
}
