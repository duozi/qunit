package com.qunar.base.qunit.model;

import com.qunar.base.qunit.annotation.Element;
import com.qunar.base.qunit.model.KeyValueStore;

import java.util.List;

/**
 * User: zhaohuiyu
 * Date: 6/26/12
 * Time: 11:46 AM
 */
public class TestElementConfig {
    public List<KeyValueStore> getParams() {
        return params;
    }

    @Element
    List<KeyValueStore> params;
}
