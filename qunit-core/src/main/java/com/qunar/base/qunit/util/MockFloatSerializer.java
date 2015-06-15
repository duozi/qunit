package com.qunar.base.qunit.util;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by zhaohui.yu
 * 6/15/15
 */
public class MockFloatSerializer implements ObjectSerializer {

    public static MockFloatSerializer instance = new MockFloatSerializer();

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        SerializeWriter out = serializer.getWriter();

        if (object == null) {
            if (serializer.isEnabled(SerializerFeature.WriteNullNumberAsZero)) {
                out.write('0');
            } else {
                out.writeNull();
            }
            return;
        }

        float floatValue = ((Float) object).floatValue();

        if (Float.isNaN(floatValue)) {
            out.writeNull();
        } else if (Float.isInfinite(floatValue)) {
            out.writeNull();
        } else {
            String floatText = Float.toString(floatValue);
            if (floatText.endsWith(".0")) {
                floatText = floatText.substring(0, floatText.length() - 2);
            }
            out.write(floatText);


        }
    }
}
