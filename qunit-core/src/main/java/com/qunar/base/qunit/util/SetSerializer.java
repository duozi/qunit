package com.qunar.base.qunit.util;

import com.alibaba.fastjson.serializer.*;

import java.io.IOException;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;

/**
 * User: zonghuang
 * Date: 4/24/14
 */
public class SetSerializer implements ObjectSerializer {
    public final static CollectionSerializer instance = new CollectionSerializer();

    @Override
    public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
        SerializeWriter out = serializer.getWriter();

        if (object == null) {
            if (out.isEnabled(SerializerFeature.WriteNullListAsEmpty)) {
                out.write("[]");
            } else {
                out.writeNull();
            }
            return;
        }

        Type elementType = null;
        if (serializer.isEnabled(SerializerFeature.WriteClassName)) {
            if (fieldType instanceof ParameterizedType) {
                ParameterizedType param = (ParameterizedType) fieldType;
                elementType = param.getActualTypeArguments()[0];
            }
        }

        Collection<?> collection = (Collection<?>) object;

        SerialContext context = serializer.getContext();
        serializer.setContext(context, object, fieldName);

        try {
            int i = 0;
            out.append('[');
            for (Object item : collection) {

                if (i++ != 0) {
                    out.append(',');
                }

                if (item == null) {
                    out.writeNull();
                    continue;
                }

                Class<?> clazz = item.getClass();

                if (clazz == Integer.class) {
                    out.writeInt(((Integer) item).intValue());
                    continue;
                }

                if (clazz == Long.class) {
                    out.writeLong(((Long) item).longValue());

                    continue;
                }

                ObjectSerializer itemSerializer = serializer.getObjectWriter(clazz);
                itemSerializer.write(serializer, item, i - 1, elementType);
            }
            out.append(']');
        } finally {
            serializer.setContext(context);
        }
    }
}
