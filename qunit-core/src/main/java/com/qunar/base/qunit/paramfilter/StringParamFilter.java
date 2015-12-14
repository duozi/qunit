package com.qunar.base.qunit.paramfilter;

import com.alibaba.fastjson.JSONObject;
import com.qunar.base.qunit.util.Util;
import org.apache.commons.lang.StringUtils;

import java.util.Iterator;
import java.util.Map;
import java.util.Random;

/**
 * User: zhaohuiyu
 * Date: 6/26/12
 * Time: 3:02 PM
 */
public class StringParamFilter extends ParamFilter {

    private static final String PREFIX = "str:";

    String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    Random random = new Random();

    @Override
    protected String doHandle(String param) {
        if (Util.isJson(param)) {
            return setStringParam(param);
        } else if (param.startsWith(PREFIX)) {
            return generateString(param);
        } else {
            return param;
        }
    }

    @Override
    protected boolean support(String param) {
        return param.startsWith(PREFIX) || param.contains(PREFIX);
    }

    protected char getRandomStr() {
        int index = random.nextInt(base.length());
        return base.charAt(index);
    }

    private String generateString(String param) {
        String countStr = param.substring(PREFIX.length());
        Integer count = Integer.valueOf(countStr);
        if (count <= 0) return StringUtils.EMPTY;
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; ++i) {
            sb.append(getRandomStr());
        }
        return sb.toString();
    }

    private String setStringParam(String param) {
        Map map = null;
        try {
            map = (Map) JSONObject.parse(param);
        } catch (Exception e) {
            return param;
        }
        Iterator iterator = map.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry entry = (Map.Entry) iterator.next();
            if (entry == null || entry.getValue() == null) continue;
            if (!(entry.getValue() instanceof String)) continue;

            if (((String) entry.getValue()).startsWith(PREFIX)) {
                map.put(entry.getKey(), generateString((String) entry.getValue()));
            }
        }
        return JSONObject.toJSONString(map);
    }

}
