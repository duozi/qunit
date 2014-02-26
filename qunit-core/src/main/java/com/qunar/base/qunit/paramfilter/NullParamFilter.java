package com.qunar.base.qunit.paramfilter;

import com.alibaba.fastjson.JSONObject;
import com.qunar.base.qunit.util.Util;

import java.util.Iterator;
import java.util.Map;

/**
 * User: zhaohuiyu
 * Date: 6/26/12
 * Time: 3:26 PM
 */
public class NullParamFilter extends ParamFilter {
    @Override
    protected String doHandle(String param) {
        if (Util.isJson(param)) {
            String json = setNullParam(param);
            return json;
        } else if (param.equals("NULL") || param.equals("[NULL]")){
            return null;
        } else {
            return param;
        }
    }

    @Override
    protected boolean support(String param) {
        return param.equals("NULL") || param.equals("[NULL]") || param.contains("[NULL]");
    }

    private String setNullParam(String param) {
        Map map = null;
        try {
            map = (Map) JSONObject.parse(param);
        } catch (Exception e) {
            return param;
        }
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if ("[NULL]".equals(entry.getValue())) {
                map.put(entry.getKey(), null);
            }
        }
        return JSONObject.toJSONString(map);
    }

}
