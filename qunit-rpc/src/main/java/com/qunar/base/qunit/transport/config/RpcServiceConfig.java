package com.qunar.base.qunit.transport.config;


import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.transport.command.ExecuteCommand;
import com.qunar.base.qunit.transport.command.RpcExecuteCommand;
import com.qunar.base.qunit.transport.model.ServiceDesc;

public class RpcServiceConfig extends ServiceConfig {
    public final static String name = "rpc";

    @Property("class")
    private String className;

    @Property
    private String method;

    @Property
    private String url;

    @Property
    private String version;

    @Property
    private String group;

    @Override
    public ExecuteCommand createCommand() {
        ServiceDesc serviceDesc = new ServiceDesc(this.className, this.method, this.url, this.version, this.group);
        return new RpcExecuteCommand(this.id, serviceDesc, this.desc);
    }

}
