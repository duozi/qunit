package com.qunar.base.qunit.context;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 6/14/13
 * Time: 12:00 PM
 */
public class ContextTest {
    private Context context;

    @Before
    public void setUp() {
        context = new Context(null);
        context.addContext("validStr", "test");
    }

    @Test
    public void replaced_expression_equals_self() {
        Object result = context.replace("${validStr}");

        assertThat(result.toString(), is("test"));
    }

    @Test
    public void replaced_expression_more_than_self() {
        Object result = context.replace("this is ${validStr}");

        assertThat(result.toString(), is("this is test"));
    }

    @Test
    public void should_get_value_for_() {
        Object result = context.replace("$validStr");

        assertThat(result.toString(), is("test"));
    }
}
