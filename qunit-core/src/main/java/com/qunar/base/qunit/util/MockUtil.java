package com.qunar.base.qunit.util;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeConfig;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

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
        return toJSONString(object, config, SerializerFeature.NotWriteRootClassName,
                SerializerFeature.WriteClassName,
                SerializerFeature.QuoteFieldNames
        ).replaceAll("@type", "class");
    }

    public static String toJSONString(Object object, SerializeConfig config, SerializerFeature... features) {
        SerializeWriter out = new SerializeWriter();

        try {
            JSONSerializer serializer = new MockJsonSerializer(out, config);
            for (com.alibaba.fastjson.serializer.SerializerFeature feature : features) {
                serializer.config(feature, true);
            }

            serializer.write(object);

            return out.toString();
        } finally {
            out.close();
        }
    }

}
