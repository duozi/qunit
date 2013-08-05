package com.qunar.base.qunit.service;

/**
 * User: zhaohuiyu
 * Date: 7/4/12
 * Time: 5:13 PM
 */
public class TestParameterService {
    public String getCount() {
        return "{\"count\":10}";
    }

    public String getString(String input) {
        return input;
    }
}
