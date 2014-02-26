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
public class EmptyParamFilter extends ParamFilter {
    @Override
    protected String doHandle(String param) {
        if (Util.isJson(param)) {
            return setEmptyParam(param);
        } else if (param.equals("[EMPTY]")){
            return "";
        } else {
            return param;
        }
    }

    @Override
    protected boolean support(String param) {
        return param != null && (param.equals("[EMPTY]") || param.contains("[EMPTY]"));
    }

    private String setEmptyParam(String param) {
        Map map = null;
        try {
            map = (Map) JSONObject.parse(param);
        } catch (Exception e) {
            return param;
        }
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if ("[EMPTY]".equals(entry.getValue())) {
                map.put(entry.getKey(), "");
            }
        }
        return JSONObject.toJSONString(map);
    }

}
