package com.qunar.base.qunit.util;

import com.qunar.base.qunit.model.KeyValueStore;
import org.junit.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static junit.framework.Assert.assertFalse;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 5/14/13
 * Time: 11:12 AM
 */
public class CloneUtilTest {
    @Test
    public void should_clone_simple_keyvalue() {
        List<KeyValueStore> parameters = new ArrayList<KeyValueStore>();
        parameters.add(new KeyValueStore("test", "test"));

        List<KeyValueStore> newParameters = CloneUtil.cloneKeyValueStore(parameters);

        assertFalse(parameters == newParameters);
        assertFalse(parameters.get(0) == newParameters.get(0));
    }

    @Test
    public void should_clone_keyvalue_nested_list() {
        List<KeyValueStore> parameters = new ArrayList<KeyValueStore>();
        List<Map<String, String>> nestedParameters = new ArrayList<Map<String, String>>();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("test1", "test1");
        nestedParameters.add(map);
        parameters.add(new KeyValueStore("test", nestedParameters));

        List<KeyValueStore> newParameters = CloneUtil.cloneKeyValueStore(parameters);

        assertFalse(parameters == newParameters);
        assertFalse(parameters.get(0) == newParameters.get(0));
        assertFalse(parameters.get(0).getValue() == newParameters.get(0).getValue());
        assertThat(newParameters.get(0).getValue(), instanceOf(List.class));
    }
}
