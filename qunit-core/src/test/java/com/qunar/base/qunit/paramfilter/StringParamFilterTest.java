package com.qunar.base.qunit.paramfilter;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.apache.commons.lang.StringUtils.EMPTY;
import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 7/17/12
 * Time: 4:59 PM
 */
public class StringParamFilterTest {

    private StringParamFilter filter;

    @Before
    public void setUp() throws Exception {
        filter = new StringParamFilter();
    }

    @Test
    public void should_support_prefix_with_str_and_comma() {
        assertTrue(filter.support("str:10"));
    }

    @Test
    public void should_not_support_other_prefix_not_str() {
        assertFalse(filter.support("string:10"));
    }

    @Test
    public void should_not_support_given_lost_comma() {
        assertFalse(filter.support("str10"));
    }

    @Test
    public void should_generate_given_number_chars() {
        String result = (String)filter.handle("str:3");
        assertThat(result.length(), Is.is(3));
    }

    @Test
    public void should_generate_given_number_chars_and_not_a_same_char() {
        String result = (String)filter.handle("str:3");
        String[] split = result.split(result.charAt(0) + "");
        assertThat(split.length, not(is(0)));
    }

    @Test
    public void should_generate_empty_string_given_zero() {
        assertThat(filter.handle("str:0").toString(), Is.is(EMPTY));
    }

    @Test
    public void should_generate_empty_string_given_negtive_number() {
        assertThat(filter.handle("str:-1").toString(), Is.is(EMPTY));
    }
}
