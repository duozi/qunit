package com.qunar.base.qunit.transport.config;

import com.qunar.base.qunit.transport.command.ExecuteCommand;
import com.qunar.base.qunit.transport.command.SHttpExecuteCommand;

/**
 * User: zhaohuiyu
 * Date: 3/29/13
 * Time: 4:47 PM
 */
public class SHttpServiceConfig extends HttpServiceConfig {
    public final static String name = "shttp";

    @Override
    public ExecuteCommand createCommand() {
        return new SHttpExecuteCommand(this.id, this.url, this.method, this.desc);
    }
}
