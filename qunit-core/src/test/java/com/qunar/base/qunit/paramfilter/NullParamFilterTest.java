package com.qunar.base.qunit.paramfilter;

import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNull;

/**
 * User: zhaohuiyu
 * Date: 7/17/12
 * Time: 5:15 PM
 */
public class NullParamFilterTest {

    private NullParamFilter paramFilter;

    @Before
    public void setUp() throws Exception {
        paramFilter = new NullParamFilter();
    }

    @Test
    public void should_support_NULL() {
        assertTrue(paramFilter.support("NULL"));
    }

    @Test
    public void should_not_support_lowercase_null() {
        assertFalse(paramFilter.support("null"));
    }

    @Test
    public void should_return_null_given_NULL_string() {
        assertNull(paramFilter.handle("NULL"));
    }
}
