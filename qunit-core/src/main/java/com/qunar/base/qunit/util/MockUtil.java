package com.qunar.base.qunit.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.*;

/**
 * User: zonghuang
 * Date: 4/24/14
 */
public class MockUtil {

    public static String toJson(Object object) {
        SerializeConfig config = new SerializeConfig();
        SetSerializer value = new SetSerializer();
        config.put(Set.class, value);
        config.put(HashSet.class, value);
        config.put(TreeSet.class, value);

        config.setTypeKey("class");
        return JSON.toJSONString(object, config, SerializerFeature.NotWriteRootClassName,
                SerializerFeature.WriteClassName,
                SerializerFeature.QuoteFieldNames,
                SerializerFeature.WriteNonStringKeyAsString,
                SerializerFeature.BrowserCompatible
        ).replaceAll("@type", "class");
    }

    public static void main(String[] args) {
        HashMap<Long, Hotel> map = new HashMap<Long, Hotel>();
        Hotel v = new Hotel();
        v.setName("aaa");
        map.put(1L, v);

        System.out.println(MockUtil.toJson(map));

    }

    private static class Hotel {
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }
}
