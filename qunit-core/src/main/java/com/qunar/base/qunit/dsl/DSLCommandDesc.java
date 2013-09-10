package com.qunar.base.qunit.dsl;

import com.qunar.base.qunit.config.StepConfig;
import com.qunar.base.qunit.util.CloneUtil;

import java.util.List;
import java.util.Map;

/**
 * User: zhaohuiyu
 * Date: 2/20/13
 * Time: 4:40 PM
 */
public class DSLCommandDesc implements Cloneable{
    private  String id;
    private  String desc;
    private  Boolean runOnce;
    private  List<StepConfig> children;
    private  Map<String, Map<String, Object>> data;

    public DSLCommandDesc(String id, String desc, Boolean runOnce, List<StepConfig> children, Map<String, Map<String, Object>> data) {
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

    public Map<String, Map<String, Object>> data(){
        return data;
    }

    public Object clone(){
        DSLCommandDesc dslCommandDesc = null;
        try {
            dslCommandDesc = (DSLCommandDesc) super.clone();
        } catch (CloneNotSupportedException e) {

        }

        dslCommandDesc.children = CloneUtil.cloneStepConfig(children);

        return dslCommandDesc;
    }
}
