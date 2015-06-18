package com.qunar.base.qunit.fastjson;

import com.alibaba.fastjson.serializer.DoubleSerializer;
import com.alibaba.fastjson.serializer.JSONSerializer;
import com.alibaba.fastjson.serializer.SerializeWriter;
import com.alibaba.fastjson.serializer.SerializerFeature;

import java.io.IOException;
import java.lang.reflect.Type;

/**
 * User: zhaohuiyu
 * Date: 8/28/13
 * Time: 1:00 PM
 */
public class QunitDoubleSerializer extends DoubleSerializer {

    public static final QunitDoubleSerializer INSTANCE = new QunitDoubleSerializer();

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType, int features) throws IOException {
        SerializeWriter out = serializer.getWriter();

        if (object == null) {
            if (serializer.isEnabled(SerializerFeature.WriteNullNumberAsZero)) {
                out.write('0');
            } else {
                out.writeNull();
            }
            return;
        }

        double doubleValue = ((Double) object).doubleValue();

        if (Double.isNaN(doubleValue)) {
            out.writeNull();
        } else if (Double.isInfinite(doubleValue)) {
            out.writeNull();
        } else {
            String doubleText = Double.toString(doubleValue);
            out.append(doubleText);

            if (serializer.isEnabled(SerializerFeature.WriteClassName)) {
                out.write('D');
            }
        }
    }

}
