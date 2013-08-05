package com.qunar.base.qunit.objectfactory;

import org.junit.Before;
import org.junit.Test;

import java.util.Map;

import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 11/5/12
 */
public class MapFactoryTest {

    private MapFactory factory;

    @Before
    public void stepUp() {
        factory = new MapFactory();
    }

    @Test
    public void should_create_map_from_json_given_provide_json_type() {
        String json = "{\"admin\":{\"name\":\"Q1\",\"__type\":\"com.qunar.base.qunit.objectfactory.Person\"},\"guest\":{\"name\":\"Q1\",\"__type\":\"com.qunar.base.qunit.objectfactory.Person\"}}";

        Map<String, Person> map = (Map<String, Person>) factory.create(Map.class, json);

        assertThat(map.size(), is(2));
        assertThat(map, hasEntry("admin", new Person("Q1")));
    }

    @Test
    public void should_create_map_from_json_given_value_is_array() {
        String json = "{\"admin\":[{\"name\":\"Q1\"}],\"guest\":[{\"name\":\"Q1\"}],\"__valueClass\":\"[Lcom.qunar.base.qunit.objectfactory.Person;\"}";

        Map<String, Person[]> map = (Map<String, Person[]>) factory.create(Map.class, json);

        assertThat(map.size(), is(2));
        assertThat(map, hasEntry("admin", new Person[]{new Person("Q1")}));
    }

    @Test
    public void should_create_map_given_key_is_enum() {
        String json = "{\"MALE\":\"10\",\"FEMALE\":\"20\",\"__keyClass\":\"com.qunar.base.qunit.objectfactory.Sex\"}";

        Map<Sex, String> map = (Map<Sex, String>) factory.create(Map.class, json);

        assertThat(map.size(), is(2));
        assertThat(map, hasEntry(Sex.MALE, "10"));
    }
}
