package com.qunar.base.qunit.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import com.alibaba.fastjson.serializer.*;
import com.alibaba.fastjson.util.FieldInfo;
import com.alibaba.fastjson.util.TypeUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * User: zonghuang
 * Date: 4/24/14
 */
public class MockUtil {

    public static String toJson(Object object) {
        SerializeConfig config = new MockSerializeConfig();
        SetSerializer value = new SetSerializer();
        config.put(BigDecimal.class, MockBigDecimalSerializer.instance);
        config.put(Byte.class, MockByteSerializer.instance);
        config.put(Double.class, MockDoubleSerializer.instance);
        config.put(Float.class, MockFloatSerializer.instance);
        config.put(Long.class, MockLongSerializer.instance);
        config.put(Short.class, MockShortSerializer.instance);
        config.put(SimpleDateFormat.class, MockDateFormatSerializer.instance);
        config.put(Set.class, value);
        config.put(HashSet.class, value);
        config.put(TreeSet.class, value);
        config.put(Date.class, MockDateSerializer.instance);
        config.put(Map.class, MockMapSerializer.instance);

        config.setTypeKey("class");
        return JSON.toJSONString(object, config, SerializerFeature.WriteClassName, SerializerFeature.QuoteFieldNames).replaceAll("@type", "class");
    }

    private static class MockSerializeConfig extends SerializeConfig {
        public ObjectSerializer createJavaBeanSerializer(Class<?> clazz) {
            return new MockJavaBeanSerializer(clazz);
        }
    }

    private static class MockJavaBeanSerializer extends JavaBeanSerializer {

        // serializers
        private final FieldSerializer[] getters;
        private final FieldSerializer[] sortedGetters;

        public FieldSerializer[] getGetters() {
            return getters;
        }

        public MockJavaBeanSerializer(Class<?> clazz) {
            this(clazz, (Map<String, String>) null);
        }

        public MockJavaBeanSerializer(Class<?> clazz, String... aliasList) {
            this(clazz, createAliasMap(aliasList));
        }

        static Map<String, String> createAliasMap(String... aliasList) {
            Map<String, String> aliasMap = new HashMap<String, String>();
            for (String alias : aliasList) {
                aliasMap.put(alias, alias);
            }

            return aliasMap;
        }

        public MockJavaBeanSerializer(Class<?> clazz, Map<String, String> aliasMap) {
            super(clazz, aliasMap);
            {
                List<FieldSerializer> getterList = new ArrayList<FieldSerializer>();
                List<FieldInfo> fieldInfoList = TypeUtils.computeGetters(clazz, aliasMap, false);

                for (FieldInfo fieldInfo : fieldInfoList) {
                    getterList.add(createFieldSerializer(fieldInfo));
                }

                getters = getterList.toArray(new FieldSerializer[getterList.size()]);
            }
            {
                List<FieldSerializer> getterList = new ArrayList<FieldSerializer>();
                List<FieldInfo> fieldInfoList = TypeUtils.computeGetters(clazz, aliasMap, true);

                for (FieldInfo fieldInfo : fieldInfoList) {
                    getterList.add(createFieldSerializer(fieldInfo));
                }

                sortedGetters = getterList.toArray(new FieldSerializer[getterList.size()]);
            }
        }

        protected boolean isWriteClassName(JSONSerializer serializer, Object obj, Type fieldType, Object fieldName) {
            return serializer.isWriteClassName(fieldType, obj);
        }

        public void write(JSONSerializer serializer, Object object, Object fieldName, Type fieldType) throws IOException {
            SerializeWriter out = serializer.getWriter();

            if (object == null) {
                out.writeNull();
                return;
            }

            if (serializer.containsReference(object)) {
                writeReference(serializer, object);
                return;
            }

            final FieldSerializer[] getters;

            if (out.isEnabled(SerializerFeature.SortField)) {
                getters = this.sortedGetters;
            } else {
                getters = this.getters;
            }

            SerialContext parent = serializer.getContext();
            serializer.setContext(parent, object, fieldName);

            final boolean writeAsArray = serializer.isWriteAsArray(object, fieldType);

            try {
                final char startSeperator = writeAsArray ? '[' : '{';
                final char endSeperator = writeAsArray ? ']' : '}';
                out.append(startSeperator);

                if (getters.length > 0 && out.isEnabled(SerializerFeature.PrettyFormat)) {
                    serializer.incrementIndent();
                    serializer.println();
                }

                boolean commaFlag = false;

                if (isWriteClassName(serializer, object, fieldType, fieldName)) {
                    Class<?> objClass = object.getClass();
                    out.writeFieldName(JSON.DEFAULT_TYPE_KEY);
                    serializer.write(object.getClass());
                    commaFlag = true;

                }

                char seperator = commaFlag ? ',' : '\0';

                char newSeperator = FilterUtils.writeBefore(serializer, object, seperator);
                commaFlag = newSeperator == ',';

                for (int i = 0; i < getters.length; ++i) {
                    FieldSerializer fieldSerializer = getters[i];

                    if (serializer.isEnabled(SerializerFeature.SkipTransientField)) {
                        Field field = fieldSerializer.getField();
                        if (field != null) {
                            if (Modifier.isTransient(field.getModifiers())) {
                                continue;
                            }
                        }
                    }

                    if (!FilterUtils.applyName(serializer, object, fieldSerializer.getName())) {
                        continue;
                    }

                    Object propertyValue = fieldSerializer.getPropertyValue(object);

                    if (!FilterUtils.apply(serializer, object, fieldSerializer.getName(), propertyValue)) {
                        continue;
                    }

                    String key = FilterUtils.processKey(serializer, object, fieldSerializer.getName(), propertyValue);

                    Object originalValue = propertyValue;
                    propertyValue = FilterUtils.processValue(serializer, object, fieldSerializer.getName(), propertyValue);

                    if (propertyValue == null && !writeAsArray) {
                        if ((!fieldSerializer.isWriteNull())
                                && (!serializer.isEnabled(SerializerFeature.WriteMapNullValue))) {
                            continue;
                        }
                    }

                    if (commaFlag) {
                        out.append(',');
                        if (out.isEnabled(SerializerFeature.PrettyFormat)) {
                            serializer.println();
                        }
                    }

                    if (key != fieldSerializer.getName()) {
                        if (!writeAsArray) {
                            out.writeFieldName(key);
                        }
                        serializer.write(propertyValue);
                    } else if (originalValue != propertyValue) {
                        if (!writeAsArray) {
                            fieldSerializer.writePrefix(serializer);
                        }
                        serializer.write(propertyValue);
                    } else {
                        if (!writeAsArray) {
                            fieldSerializer.writeProperty(serializer, propertyValue);
                        } else {
                            fieldSerializer.writeValue(serializer, propertyValue);
                        }
                    }

                    commaFlag = true;
                }

                FilterUtils.writeAfter(serializer, object, commaFlag ? ',' : '\0');

                if (getters.length > 0 && out.isEnabled(SerializerFeature.PrettyFormat)) {
                    serializer.decrementIdent();
                    serializer.println();
                }

                out.append(endSeperator);
            } catch (Exception e) {
                throw new JSONException("write javaBean error", e);
            } finally {
                serializer.setContext(parent);
            }
        }

        public void writeReference(JSONSerializer serializer, Object object) {
            serializer.writeReference(object);
        }

        public FieldSerializer createFieldSerializer(FieldInfo fieldInfo) {
            Class<?> clazz = fieldInfo.getFieldClass();

            if (clazz == Number.class) {
                return new NumberFieldSerializer(fieldInfo);
            }

            return new ObjectFieldSerializer(fieldInfo);
        }


    }

    private static class NumberFieldSerializer extends FieldSerializer {

        public NumberFieldSerializer(FieldInfo fieldInfo) {
            super(fieldInfo);
        }

        public void writeProperty(JSONSerializer serializer, Object propertyValue) throws Exception {
            writePrefix(serializer);
            this.writeValue(serializer, propertyValue);
        }

        @Override
        public void writeValue(JSONSerializer serializer, Object propertyValue) throws Exception {
            SerializeWriter out = serializer.getWriter();

            Object value = propertyValue;

            if (value == null) {
                if (out.isEnabled(SerializerFeature.WriteNullNumberAsZero)) {
                    out.write('0');
                } else {
                    out.writeNull();
                }
                return;
            }

            out.append(value.toString());
        }
    }
}
