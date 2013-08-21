package com.qunar.base.qunit.dsl;

import com.qunar.base.qunit.config.StepConfig;

import java.util.List;
import java.util.Map;

/**
 * User: zhaohuiyu
 * Date: 2/20/13
 * Time: 4:40 PM
 */
public class DSLCommandDesc {
    private final String id;
    private final String desc;
    private final Boolean runOnce;
    private final List<StepConfig> children;
    private final Map<String, Map<String, String>> data;

    public DSLCommandDesc(String id, String desc, Boolean runOnce, List<StepConfig> children, Map<String, Map<String, String>> data) {
        this.id = id;
        this.desc = desc;
        this.runOnce = runOnce;
        this.children = children;
        this.data = data;
    }

    public String id() {
        return this.id;
    }

    public String desc() {
        return desc;
    }

    public Boolean runOnce() {
        return runOnce;
    }

    public List<StepConfig> children() {
        return children;
    }

    public Map<String, Map<String, String>> data(){
        return data;
    }
}
