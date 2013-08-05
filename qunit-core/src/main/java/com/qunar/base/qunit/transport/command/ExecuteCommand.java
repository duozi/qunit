package com.qunar.base.qunit.transport.command;

import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.model.ServiceDesc;
import com.qunar.base.qunit.reporter.Reporter;
import com.qunar.base.qunit.response.Response;

import java.util.List;

public abstract class ExecuteCommand {

    protected String id;
    protected String desc;

    public ExecuteCommand(String id, String desc) {
        this.id = id;
        this.desc = desc;
    }

    public String getId() {
        return this.id;
    }

    public String getDesc() {
        return this.desc;
    }

    public abstract Response execute(List<KeyValueStore> params);

    public abstract String toReport();

    public abstract ServiceDesc desc();

}
