package com.qunar.base.qunit.util;

import com.qunar.base.qunit.model.MyUser;
import org.junit.Test;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import static org.hamcrest.Matchers.typeCompatibleWith;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 6/10/12
 * Time: 6:26 PM
 */
public class ReflectionUtilsTest {
    @Test
    public void should_getAllFields_from_user_defined_type() {
        List<Field> fields = ReflectionUtils.getAllFields(User.class);

        assertThat(fields.size(), is(2));
    }

    @Test
    public void should_getAllFields_from_subtype() {
        List<Field> fields = ReflectionUtils.getAllFields(MyUser.class);

        assertThat(fields.size(), is(4));
    }

    @Test
    public void should_set_field_value_through_setter() throws NoSuchFieldException {
        MyUser user = new MyUser();

        Field field = user.getClass().getDeclaredField("no");
        ReflectionUtils.setFieldValue(user, field, "12345");

        assertThat(user.getMyname(), is("12345"));
    }

    @Test
    public void should_object_super_class_is_null() {
        assertNull(Object.class.getSuperclass());
    }

    @Test
    public void should_get_method_by_methodname_and_parameters() {
        User user = new User();
        Method method = ReflectionUtils.getMethod(user, "setAddress(Address,String)");

        assertThat(method.getName(), is("setAddress"));
        Class<?>[] parameterTypes = method.getParameterTypes();
        assertThat(parameterTypes.length, is(2));
        assertThat(parameterTypes[0], is(typeCompatibleWith(Address.class)));
    }

    @Test
    public void should_get_method_by_methodname_and_parameters_with_basic_type() {
        User user = new User();
        Method method = ReflectionUtils.getMethod(user, "setAddress(Address,int)");

        assertThat(method.getName(), is("setAddress"));
        Class<?>[] parameterTypes = method.getParameterTypes();
        assertThat(parameterTypes.length, is(2));
        assertThat(parameterTypes[0], is(typeCompatibleWith(Address.class)));
        assertThat(parameterTypes[1], is(typeCompatibleWith(int.class)));
    }

    @Test
    public void should_get_method_by_methodname_and_parameters_with_different_order() {
        User user = new User();
        Method method = ReflectionUtils.getMethod(user, "setAddress(String,Address)");

        assertThat(method.getName(), is("setAddress"));
        Class<?>[] parameterTypes = method.getParameterTypes();
        assertThat(parameterTypes.length, is(2));
        assertThat(parameterTypes[0], is(typeCompatibleWith(String.class)));
        assertThat(parameterTypes[1], is(typeCompatibleWith(Address.class)));
    }

    @Test
    public void should_get_method_by_methodname_and_parameters_with_space() {
        User user = new User();
        Method method = ReflectionUtils.getMethod(user, "setAddress(String,   Address)");

        assertThat(method.getName(), is("setAddress"));
        Class<?>[] parameterTypes = method.getParameterTypes();
        assertThat(parameterTypes.length, is(2));
        assertThat(parameterTypes[0], is(typeCompatibleWith(String.class)));
        assertThat(parameterTypes[1], is(typeCompatibleWith(Address.class)));
    }

    @Test
    public void should_get_method_by_methodname_and_parameters_with_space_in_methodName() {
        User user = new User();
        Method method = ReflectionUtils.getMethod(user, "setAddress  (String,Address)");

        assertThat(method.getName(), is("setAddress"));
        Class<?>[] parameterTypes = method.getParameterTypes();
        assertThat(parameterTypes.length, is(2));
        assertThat(parameterTypes[0], is(typeCompatibleWith(String.class)));
        assertThat(parameterTypes[1], is(typeCompatibleWith(Address.class)));
    }

    @Test
    public void should_get_method_by_methodName_only() {
        User user = new User();
        Method method = ReflectionUtils.getMethod(user, "getAddress");

        assertThat(method.getName(), is("getAddress"));
    }

}
