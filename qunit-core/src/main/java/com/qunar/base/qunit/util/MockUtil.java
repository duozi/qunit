package com.qunar.base.qunit.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

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
                SerializerFeature.QuoteFieldNames
        ).replaceAll("@type", "class");
    }

    public static void main(String[] args) {
        HashMap<Long, Hotel> map = new HashMap<Long, Hotel>();
        Hotel v = new Hotel();
        v.setName("aaa");
        v.setPrice(1.0);
        map.put(1L, v);

        String s = MockUtil.toJson(map);
        System.out.println(s);
        Object parse = JSON.parse(s);


        System.out.println(parse);
        System.out.println(s);

    }

    private static class Hotel {
        private String name;

        private double price;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public double getPrice() {
            return price;
        }

        public void setPrice(double price) {
            this.price = price;
        }
    }
}
