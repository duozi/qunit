/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.config;

import com.qunar.base.qunit.annotation.Element;
import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.command.MockStepCommand;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.model.KeyValueStore;

import java.util.ArrayList;
import java.util.List;

import static com.qunar.base.qunit.model.MockInfo.*;

/**
 * 描述：
 * Created by JarnTang at 12-6-26 下午5:48
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class MockStepConfig extends StepConfig {

    //目标API
    @Property(required = true)
    String service;

    //此次调用的标识
    @Property
    String key;

    //目标api所属的业务线
    @Property(required = true)
    String target;

    //调用目标API的来源IP(一般就是被测系统)
    @Property
    String source;

    //返回值
    @Property("return")
    String returnValue;

    //回调
    @Element
    List<KeyValueStore> params;

    public List<KeyValueStore> getParams() {
        return params;
    }

    @Override
    public StepCommand createCommand() {
        List<KeyValueStore> newParams = new ArrayList<KeyValueStore>();
        newParams.addAll(params);
        newParams.add(new KeyValueStore(SOURCE, this.source));
        newParams.add(new KeyValueStore(TARGET, this.target));
        newParams.add(new KeyValueStore(KEY, this.key));
        newParams.add(new KeyValueStore(RETURN_VALUE, this.returnValue));
        newParams.add(new KeyValueStore(SERVICE, this.service));
        params = newParams;
        return new MockStepCommand(this.params);
    }

}
