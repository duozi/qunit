package com.qunar.base.qunit.util;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by zhaohui.yu
 * 6/29/15
 */
public class MockEnumSerializer implements ObjectSerializer {

    public final static MockEnumSerializer instance = new MockEnumSerializer();

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        SerializeWriter out = serializer.getWriter();
        if (object == null) {
            serializer.getWriter().writeNull();
            return;
        }

        if (object.getClass() != fieldType) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("class", object.getClass().getName());
            Enum<?> e = (Enum<?>) object;
            map.put("name", e.name());
            serializer.write(map);
        } else {
            Enum<?> e = (Enum<?>) object;
            serializer.write(e.name());
        }

    }
}
