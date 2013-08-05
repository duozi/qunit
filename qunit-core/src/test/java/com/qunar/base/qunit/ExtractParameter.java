package com.qunar.base.qunit;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.qunar.base.qunit.command.Transform;
import org.apache.commons.lang.StringUtils;

/**
 * User: zhaohuiyu
 * Date: 6/8/12
 * Time: 2:31 PM
 */
public class ExtractParameter extends Transform {
    @Override
    public Object transport(Object original) {
        JSONObject jsonObject = JSON.parseObject(original.toString());
        JSONObject rt_data = (JSONObject) jsonObject.get("rt_data");
        Object queryID = jsonObject.get("queryID");
        JSONObject packagePriceInfo = (JSONObject) rt_data.get("packagePriceInfo");
        String key = getFirst(packagePriceInfo);
        String[] split = StringUtils.split(key, "|");
        split[0] = split[0].substring(1);
        split[1] = split[1].substring(1);
        String flightCodePair = split[0] + "_" + split[1];
        return String.format("{\"queryID\":\"%s\",\"flightCodePair\":\"%s\"}", queryID.toString(), flightCodePair);
    }

    private String getFirst(JSONObject packagePriceInfo) {
        for (String key : packagePriceInfo.keySet()) {
            return key;
        }
        return null;
    }
}
