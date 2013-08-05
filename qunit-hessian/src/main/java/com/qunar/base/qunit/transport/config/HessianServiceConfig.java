package com.qunar.base.qunit.transport.config;

import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.transport.command.ExecuteCommand;
import com.qunar.base.qunit.transport.command.HessianExecuteCommand;

/**
 * User: zhaohuiyu
 * Date: 5/6/13
 * Time: 12:23 PM
 */
public class HessianServiceConfig extends ServiceConfig {
    @Property("class")
    private String interfaceName;

    @Property
    private String method;

    @Property
    private String url;


    @Override
    public ExecuteCommand createCommand() {
        return new HessianExecuteCommand(this.id, url, interfaceName, method, this.desc);
    }
}
