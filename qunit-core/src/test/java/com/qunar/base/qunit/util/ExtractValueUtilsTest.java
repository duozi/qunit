package com.qunar.base.qunit.util;

import com.qunar.base.qunit.model.Group;
import org.hamcrest.core.Is;
import org.junit.Test;

import java.util.List;

import static com.qunar.base.qunit.util.ExtractValueUtils.extractParameters;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 6/10/12
 * Time: 11:45 AM
 */
public class ExtractValueUtilsTest {
    @Test
    public void should_extract_original_object() {
        User user = new User();
        user.setUserName("yuyijq");

        Object result = ExtractValueUtils.extract("$result", user);

        assertThat((User) result, is(user));
    }

    @Test
    public void should_extract_basic_type_property_of_original_object() {
        User user = new User();
        user.setUserName("yuyijq");

        Object result = ExtractValueUtils.extract("$result.userName", user);

        assertThat((String) result, is("yuyijq"));
    }

    @Test
    public void should_extract_user_defined_type_property_of_original_object() {
        User user = new User();
        Address address = new Address();
        address.setCity("beijing");
        user.setAddress(address);

        Object result = ExtractValueUtils.extract("$result.address", user);

        assertThat((Address) result, is(address));
    }

    @Test
    public void should_extract_indirect_property_of_original_object() {
        User user = new User();
        Address address = new Address();
        address.setCity("beijing");
        user.setAddress(address);

        Object result = ExtractValueUtils.extract("$result.address.city", user);

        assertThat((String) result, is("beijing"));
    }

    @Test
    public void should_extract_array_element_from_original_array() {
        User user = new User();
        user.setUserName("yuyijq");
        User[] users = new User[]{user};

        Object result = ExtractValueUtils.extract("$result[0]", users);

        assertThat((User) result, is(user));
    }

    @Test
    public void should_extract_array_property_from_original_object() {
        Group group = new Group();
        User user = new User();
        user.setUserName("yuyijq");
        User[] users = new User[]{user};
        group.setUsers(users);

        Object result = ExtractValueUtils.extract("$result.users[0]", group);

        assertThat((User) result, is(user));
    }

    @Test
    public void should_extract_property_of_array_property_from_original_object() {
        Group group = new Group();
        User user = new User();
        user.setUserName("yuyijq");
        User[] users = new User[]{user};
        group.setUsers(users);

        Object result = ExtractValueUtils.extract("$result.users[0].userName", group);

        assertThat((String) result, is("yuyijq"));
    }

    @Test
    public void should_extract_property_from_json() {
        String json = "{\"userName\":\"yuyijq\"}";
        Object result = ExtractValueUtils.extract("$result.userName", json);

        assertThat((String) result, is("yuyijq"));
    }

    @Test
    public void should_extract_nested_property_from_json() {
        String json = "{\"userName\":\"yuyijq\",\"address\":{\"city\":\"beijing\"}}";
        Object result = ExtractValueUtils.extract("$result.address.city", json);

        assertThat((String) result, is("beijing"));
    }

    @Test
    public void should_extract_array_element_from_json() {
        String json = "[{\"userName\":\"yuyijq1\"},{\"userName\":\"yuyijq2\"}]";

        Object result = ExtractValueUtils.extract("$result[0].userName", json);

        assertThat((String) result, is("yuyijq1"));
    }

    @Test
    public void should_extract_nested_array_element_from_json() {
        String json = "{\"name\":\"admin\",\"users\":[{\"userName\":\"yuyijq1\"}]}";

        Object result = ExtractValueUtils.extract("$result.users[0].userName", json);

        assertThat((String) result, is("yuyijq1"));
    }

    @Test
    public void should_extract_single_parameter_from_sql() {
        String sql = "select * from table1 where id = $result.orderId";

        List<String> parameters = extractParameters(sql);

        assertThat(parameters.size(), Is.is(1));
        assertThat(parameters.get(0), Is.is("$result.orderId"));
    }

    @Test
    public void should_extract_mulitple_parameters_from_sql() {
        String sql = "select * from table1 where id = $result.orderId and name = '$result.name'";

        List<String> parameters = extractParameters(sql);

        assertThat(parameters.size(), Is.is(2));
        assertThat(parameters, hasItem("$result.orderId"));
        assertThat(parameters, hasItem("$result.name"));
    }

    @Test
    public void should_extract_parameter_with_array() {
        String sql = "select * from table1 where id = $result[0].orderId";

        List<String> parameters = extractParameters(sql);

        assertThat(parameters.size(), Is.is(1));
        assertThat(parameters, hasItem("$result[0].orderId"));
    }

    @Test
    public void should_extract_parameter_with_array_in_property() {
        String sql = "select * from table1 where id = $result[0].orderId[0].id";

        List<String> parameters = extractParameters(sql);

        assertThat(parameters.size(), Is.is(1));
        assertThat(parameters, hasItem("$result[0].orderId[0].id"));
    }

    @Test
    public void should_extract_parameter_with_underline() {
        List<String> parameters = extractParameters("test $result.user_id");

        assertThat(parameters, hasItem("$result.user_id"));
    }

    @Test
    public void should_extract_parameter_with_quote() {
        List<String> parameters = extractParameters("select * from table where (id = $result.data)");

        assertThat(parameters, hasItem("$result.data"));
    }

    @Test
    public void should_extract_parameter_with_square_brackets() {
        List<String> parameters = extractParameters("select * from table where (id = $result.data)");

        assertThat(parameters, hasItem("$result.data"));
    }

    @Test
    public void should_extract_parameter_with_number() {
        List<String> parameters = extractParameters("test $result.d123");

        assertThat(parameters, hasItem("$result.d123"));
    }
}
