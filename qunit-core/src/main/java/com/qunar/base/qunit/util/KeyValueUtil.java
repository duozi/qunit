package com.qunar.base.qunit.util;

import com.qunar.base.qunit.model.KeyValueStore;

import java.util.List;

/**
 * User: zhaohuiyu
 * Date: 4/24/13
 * Time: 6:41 PM
 */
public class KeyValueUtil {
    public static String getValueByKey(String key, List<KeyValueStore> processedParams) {
        for (KeyValueStore kvs : processedParams) {
            if (key.equals(kvs.getName())) {
                return (String) kvs.getValue();
            }
        }
        return null;
    }
}
