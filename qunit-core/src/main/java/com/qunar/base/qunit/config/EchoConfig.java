package com.qunar.base.qunit.config;

import com.qunar.base.qunit.annotation.ConfigElement;
import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.command.EchoCommand;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.model.KeyValueStore;

import java.util.Arrays;

/**
 * User: zhaohuiyu
 * Date: 12/18/12
 */
@ConfigElement(defaultProperty = "value")
public class EchoConfig extends StepConfig {

    @Property
    private String value;

    @Override
    public StepCommand createCommand() {
        return new EchoCommand(Arrays.asList(new KeyValueStore("value", value)));
    }
}
