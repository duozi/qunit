package com.qunar.base.qunit.util;

import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.ObjectSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;

import java.io.IOException;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;

/**
 * Created by zhaohui.yu
 * 6/15/15
 */
public class MockDateFormatSerializer implements ObjectSerializer {

    public final static MockDateFormatSerializer instance = new MockDateFormatSerializer();

    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        SerializeWriter out = serializer.getWriter();

        if (object == null) {
            out.writeNull();
            return;
        }

        String pattern = ((SimpleDateFormat) object).toPattern();

        out.writeString(pattern);
    }
}

