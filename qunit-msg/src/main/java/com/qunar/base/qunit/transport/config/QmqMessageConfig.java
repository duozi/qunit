package com.qunar.base.qunit.transport.config;

import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.transport.command.ExecuteCommand;
import com.qunar.base.qunit.transport.command.QmqMessageExecuteCommand;

/**
 * User: zhaohuiyu
 * Date: 9/4/13
 * Time: 4:09 PM
 */
public class QmqMessageConfig extends ServiceConfig {
    @Property
    private String subject;

    @Property
    private String host;

    @Property
    private String group;

    @Override
    public ExecuteCommand createCommand() {
        return new QmqMessageExecuteCommand(this.id, this.desc, subject, host, group);
    }
}
