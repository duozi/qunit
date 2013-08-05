/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.intercept;

import com.qunar.base.qunit.model.KeyValueStore;

import java.util.ArrayList;
import java.util.List;

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
                if (IGNORE_PARAM.equals(kvs.getValue())) {
                    list.remove(kvs);
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
                if (IGNORE_PARAM.equals(kvs.getValue())) {
                    return true;
                }
            }
        }
        return false;
    }

}
