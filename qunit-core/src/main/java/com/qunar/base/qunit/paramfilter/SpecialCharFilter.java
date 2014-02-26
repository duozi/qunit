/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.paramfilter;

import com.alibaba.fastjson.JSONObject;
import com.qunar.base.qunit.util.Util;

import java.util.Iterator;
import java.util.Map;

import static com.qunar.base.qunit.util.PropertyUtils.getProperty;

/**
 * 描述：
 * Created by JarnTang at 12-8-8 上午11:38
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class SpecialCharFilter extends ParamFilter{

    private static final String SPECIAL_CHAR = "!@#$%^&*()_+-<>?/'|\"\\{}[]~`";

    String specialChar;

    public SpecialCharFilter(){
        specialChar = getProperty("specialChar", SPECIAL_CHAR);
        if (!SPECIAL_CHAR.equals(specialChar)) {
            logger.info(String.format("load special char [%s] from config file.", specialChar));
        }
    }

    @Override
    protected String doHandle(String param) {
        if (Util.isJson(param)) {
            return setSpecialParam(param);
        } else if ("[SPECIAL]".equals(param)){
            return specialChar;
        } else {
            return param;
        }
    }

    @Override
    protected boolean support(String param) {
        return "[SPECIAL]".equals(param) || param.contains("[SPECIAL]");
    }

    private String setSpecialParam(String param) {
        Map map = null;
        try {
            map = (Map) JSONObject.parse(param);
        } catch (Exception e) {
            return param;
        }
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if ("[SPECIAL]".equals(entry.getValue())) {
                map.put(entry.getKey(), specialChar);
            }
        }
        return JSONObject.toJSONString(map);
    }

}
