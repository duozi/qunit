package com.qunar.base.qunit.config;

import com.qunar.base.qunit.annotation.ConfigElement;
import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.command.ExamplesCommand;
import com.qunar.base.qunit.command.StepCommand;

import java.io.IOException;

/**
 * User: zhaohuiyu
 * Date: 10/16/12
 */
@ConfigElement(defaultProperty = ExamplesConfig.BODY_TAG_NAME)
public class ExamplesConfig extends StepConfig {
    public static final String BODY_TAG_NAME = "body";

    @Property
    private String body;

    @Override
    public StepCommand createCommand() {
        try {
            return new ExamplesCommand(body);
        } catch (IOException e) {
            return null;
        }
    }
}
