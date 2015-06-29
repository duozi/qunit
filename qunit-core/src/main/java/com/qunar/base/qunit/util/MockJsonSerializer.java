package com.qunar.base.qunit.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONAware;
import com.alibaba.fastjson.JSONStreamAware;
import com.alibaba.fastjson.serializer.*;
import com.alibaba.fastjson.util.ServiceLoader;

import java.lang.reflect.Proxy;
import java.lang.reflect.Type;
import java.nio.charset.Charset;
import java.sql.Clob;
import java.util.*;

/**
 * Created by zhaohui.yu
 * 6/29/15
 */
public class MockJsonSerializer extends JSONSerializer {

    private final SerializeConfig config;

    public MockJsonSerializer() {
        this(new SerializeWriter(), SerializeConfig.getGlobalInstance());
    }

    public MockJsonSerializer(SerializeWriter out) {
        this(out, SerializeConfig.getGlobalInstance());
    }

    public MockJsonSerializer(SerializeConfig config) {
        this(new SerializeWriter(), config);
    }

    @Deprecated
    public MockJsonSerializer(JSONSerializerMap mapping) {
        this(new SerializeWriter(), mapping);
    }

    public MockJsonSerializer(SerializeWriter out, SerializeConfig config) {
        super(out, config);
        this.config = config;
    }

    public ObjectSerializer getObjectWriter(Class<?> clazz) {
        ObjectSerializer writer = config.get(clazz);

        if (writer == null) {
            try {
                final ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
                for (Object o : ServiceLoader.load(AutowiredObjectSerializer.class, classLoader)) {
                    if (!(o instanceof AutowiredObjectSerializer)) {
                        continue;
                    }

                    AutowiredObjectSerializer autowired = (AutowiredObjectSerializer) o;
                    for (Type forType : autowired.getAutowiredFor()) {
                        config.put(forType, autowired);
                    }
                }
            } catch (ClassCastException ex) {
                // skip
            }

            writer = config.get(clazz);
        }

        if (writer == null) {
            final ClassLoader classLoader = JSON.class.getClassLoader();
            if (classLoader != Thread.currentThread().getContextClassLoader()) {
                try {
                    for (Object o : ServiceLoader.load(AutowiredObjectSerializer.class, classLoader)) {

                        if (!(o instanceof AutowiredObjectSerializer)) {
                            continue;
                        }

                        AutowiredObjectSerializer autowired = (AutowiredObjectSerializer) o;
                        for (Type forType : autowired.getAutowiredFor()) {
                            config.put(forType, autowired);
                        }
                    }
                } catch (ClassCastException ex) {
                    // skip
                }

                writer = config.get(clazz);
            }
        }

        if (writer == null) {
            if (Map.class.isAssignableFrom(clazz)) {
                config.put(clazz, MapSerializer.instance);
            } else if (List.class.isAssignableFrom(clazz)) {
                config.put(clazz, ListSerializer.instance);
            } else if (Collection.class.isAssignableFrom(clazz)) {
                config.put(clazz, CollectionSerializer.instance);
            } else if (Date.class.isAssignableFrom(clazz)) {
                config.put(clazz, DateSerializer.instance);
            } else if (JSONAware.class.isAssignableFrom(clazz)) {
                config.put(clazz, JSONAwareSerializer.instance);
            } else if (JSONStreamAware.class.isAssignableFrom(clazz)) {
                config.put(clazz, JSONStreamAwareSerializer.instance);
            } else if (clazz.isEnum() || (clazz.getSuperclass() != null && clazz.getSuperclass().isEnum())) {
                config.put(clazz, MockEnumSerializer.instance);
            } else if (clazz.isArray()) {
                Class<?> componentType = clazz.getComponentType();
                ObjectSerializer compObjectSerializer = getObjectWriter(componentType);
                config.put(clazz, new ArraySerializer(componentType, compObjectSerializer));
            } else if (Throwable.class.isAssignableFrom(clazz)) {
                config.put(clazz, new ExceptionSerializer(clazz));
            } else if (TimeZone.class.isAssignableFrom(clazz)) {
                config.put(clazz, TimeZoneSerializer.instance);
            } else if (Appendable.class.isAssignableFrom(clazz)) {
                config.put(clazz, AppendableSerializer.instance);
            } else if (Charset.class.isAssignableFrom(clazz)) {
                config.put(clazz, CharsetSerializer.instance);
            } else if (Enumeration.class.isAssignableFrom(clazz)) {
                config.put(clazz, EnumerationSeriliazer.instance);
            } else if (Calendar.class.isAssignableFrom(clazz)) {
                config.put(clazz, CalendarSerializer.instance);
            } else if (Clob.class.isAssignableFrom(clazz)) {
                config.put(clazz, ClobSeriliazer.instance);
            } else {
                boolean isCglibProxy = false;
                boolean isJavassistProxy = false;
                for (Class<?> item : clazz.getInterfaces()) {
                    if (item.getName().equals("net.sf.cglib.proxy.Factory")) {
                        isCglibProxy = true;
                        break;
                    } else if (item.getName().equals("javassist.util.proxy.ProxyObject")) {
                        isJavassistProxy = true;
                        break;
                    }
                }

                if (isCglibProxy || isJavassistProxy) {
                    Class<?> superClazz = clazz.getSuperclass();

                    ObjectSerializer superWriter = getObjectWriter(superClazz);
                    config.put(clazz, superWriter);
                    return superWriter;
                }

                if (Proxy.isProxyClass(clazz)) {
                    config.put(clazz, config.createJavaBeanSerializer(clazz));
                } else {
                    config.put(clazz, config.createJavaBeanSerializer(clazz));
                }
            }

            writer = config.get(clazz);
        }
        return writer;
    }
}
