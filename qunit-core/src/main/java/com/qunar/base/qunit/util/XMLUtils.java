package com.qunar.base.qunit.util;

import com.qunar.base.qunit.model.KeyValueStore;
import org.dom4j.Attribute;
import org.dom4j.Element;

import java.util.*;

/**
 * User: zonghuang
 * Date: 9/5/13
 */
public class XMLUtils {

    public static List<KeyValueStore> getAttribute(Element element) {
        List<KeyValueStore> attributes = new ArrayList<KeyValueStore>();
        Iterator iterator = element.attributeIterator();
        while (iterator.hasNext()) {
            Attribute attribute = (Attribute) iterator.next();
            String attributeName = attribute.getName();
            String attributeValue = attribute.getValue();
            attributes.add(new KeyValueStore(attributeName, attributeValue));
        }
        return attributes;
    }

    public static Map<String, String> getAttributeMap(Element element) {
        List<KeyValueStore> attribute = getAttribute(element);
        return convertListKeyValueToMap(attribute);
    }

    public static Map<String, String> convertListKeyValueToMap(List<KeyValueStore> list) {
        Map<String, String> map = new HashMap<String, String>();
        for (KeyValueStore kvs : list) {
            map.put(kvs.getName(), (String) kvs.getValue());
        }
        return map;
    }
}
