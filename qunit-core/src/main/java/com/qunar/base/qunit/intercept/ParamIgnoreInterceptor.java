/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.intercept;

import com.alibaba.fastjson.JSONObject;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.util.Util;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * 描述：
 * Created by JarnTang at 12-8-8 上午11:22
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class ParamIgnoreInterceptor extends ParameterInterceptor {

    private static final String IGNORE_PARAM = "[IGNORE]";

    @Override
    protected List<KeyValueStore> convert(List<KeyValueStore> params) {
        if (params != null) {
            ArrayList<KeyValueStore> list = new ArrayList<KeyValueStore>(params);
            for (KeyValueStore kvs : params) {
                if (Util.isJson(kvs.getValue())) {
                    kvs.setValue(removeElement((String) kvs.getValue()));
                } else {
                    if (IGNORE_PARAM.equals(kvs.getValue())) {
                        list.remove(kvs);
                    }
                }
            }
            return list;
        }
        return params;
    }

    @Override
    protected boolean support(List<KeyValueStore> params) {
        if (params != null) {
            for (KeyValueStore kvs : params) {
                if (IGNORE_PARAM.equals(kvs.getValue()) || (Util.isJson(kvs.getValue()) && ((String)kvs.getValue()).contains(IGNORE_PARAM))) {
                    return true;
                }
            }
        }
        return false;
    }

    private String removeElement(String json) {
        Map map = null;
        try {
            map = (Map) JSONObject.parse(json);
        } catch (Exception e) {
            return json;
        }
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (IGNORE_PARAM.equals(entry.getValue())) {
                iterator.remove();
            }
        }
        return JSONObject.toJSONString(map);
    }

}
