package com.qunar.base.qunit.model;

import com.qunar.base.qunit.annotation.ConfigElement;
import com.qunar.base.qunit.annotation.Property;

/**
 * User: zhaohuiyu
 * Date: 6/11/12
 * Time: 4:38 PM
 */
@ConfigElement(defaultProperty = "text")
public class TestConfig {

    @Property
    private String text;

    @Property
    private String userName;

    public String getText() {
        return text;
    }

    public String getUserName() {
        return userName;
    }
}
