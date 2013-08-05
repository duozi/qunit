package com.qunar.base.qunit.extension;

import com.qunar.base.qunit.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

/**
 * User: zhaohuiyu
 * Date: 11/17/12
 */
public class ExtensionLoader {
    private static Logger logger = LoggerFactory.getLogger(ExtensionLoader.class);

    private final static String extensionLocation = "META-INF/qunit/";

    public static <T> Map<String, Class<? extends T>> loadExtension(Class<? extends T> type) throws IOException {
        String fileName = extensionLocation + type.getName();
        ClassLoader classLoader = findClassLoader();
        Enumeration<URL> resources = classLoader.getResources(fileName);
        Map<String, Class<? extends T>> result = new HashMap<String, Class<? extends T>>();
        while (resources.hasMoreElements()) {
            URL url = resources.nextElement();
            loadFile(url, type, result);
        }
        return result;
    }

    private static <T> void loadFile(URL url, Class type, Map<String, Class<? extends T>> result) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
        String line = null;
        while ((line = reader.readLine()) != null) {
            String[] keyValue = line.split("=");
            String name = keyValue[0];
            String value = keyValue[1];
            Class<?> clazz = ReflectionUtils.loadClass(value);
            if (!type.isAssignableFrom(clazz)) {
                logger.error("{} is not subclass of {}", value, type.getName());
                continue;
            }
            result.put(name, (Class<? extends T>) clazz);
        }
    }

    private static ClassLoader findClassLoader() {
        return ExtensionLoader.class.getClassLoader();
    }
}
