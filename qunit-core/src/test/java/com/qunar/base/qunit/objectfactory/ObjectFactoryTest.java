package com.qunar.base.qunit.objectfactory;

import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 11/13/12
 */
public class ObjectFactoryTest {
    @Test
    public void should_create_instance_from_json_given_type_has_not_default_ctor() {
        String json = "{\"name\":\"admin\",\"age\":\"20\",\"address\":{\"street\":\"beijing\"}}";

        ObjectFactory factory = new ObjectFactory();
        User user = (User) factory.create(User.class, json);

        assertThat(user.getName(), is("admin"));
    }
}
