/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.util;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import static org.apache.commons.lang.StringUtils.replace;
import static org.apache.commons.lang.StringUtils.trim;

/**
 * 配置文件内容解析
 * <p/>
 * Created by JarnTang at 12-5-23 下午1:56
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class PropertyUtils {

    static Map<String, String> configs = new HashMap<String, String>();
    static final Logger LOGGER = LoggerFactory.getLogger(PropertyUtils.class);

    static {
        loadProperties("qunit.properties");
    }

    private static void loadProperties(String propertiesFileName) {
        try {
            Properties PROPERTIES = new Properties();

            InputStream stream = PropertyUtils.class.getClassLoader().getResourceAsStream(propertiesFileName);
            if (stream == null) return;

            PROPERTIES.load(stream);
            Set<Map.Entry<Object, Object>> entries = PROPERTIES.entrySet();
            for (Map.Entry entry : entries) {
                String key = entry.getKey() == null ? null : StringUtils.trim(entry.getKey().toString());
                String value = entry.getValue() == null ? null : StringUtils.trim(entry.getValue().toString());
                configs.put(new String(key.getBytes("ISO8859-1"), "UTF-8"), new String(value.getBytes("ISO8859-1"), "UTF-8"));
            }
        } catch (IOException e) {
            LOGGER.error("load properties file has error,", e);
        }
    }

    public static String getProperty(String name) {
        return configs.get(name);
    }

    public static String getProperty(String name, String defaultValue) {
        String result = configs.get(name);
        if (StringUtils.isBlank(result)) {
            result = defaultValue;
        }
        return result;
    }

    public static String replaceConfigValue(String source) {
        if (StringUtils.isNotBlank(source)) {
            for (Map.Entry<String, String> entry : configs.entrySet()) {
                source = replace(source, "$" + trim(entry.getKey()), entry.getValue());
                source = replace(source, "${" + trim(entry.getKey()) + "}", entry.getValue());
            }
        }
        return source;
    }

}
