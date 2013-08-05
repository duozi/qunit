package com.qunar.base.qunit.paramfilter;

import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: zhaohuiyu
 * Date: 7/20/12
 * Time: 5:44 PM
 */
public class DateParamFilterTest {

    private Clock mockClock;
    private DateParamFilter filter;

    @Before
    public void setUp() {
        mockClock = mock(Clock.class);
        filter = new DateParamFilter(mockClock);
    }

    @Test
    public void should_return_now() throws ParseException {
        when(mockClock.afterDays(0)).thenReturn(new SimpleDateFormat("yyyy-MM-dd").parse("2012-07-20"));

        assertThat(filter.handle("DATE(0,yyyy-MM-dd)").toString(), Is.is("2012-07-20"));
    }

    @Test
    public void should_return_date_with_space() throws ParseException {
        when(mockClock.afterDays(0)).thenReturn(new SimpleDateFormat("yyyy-MM-dd").parse("2012-07-20"));

        assertThat(filter.handle("DATE(0,  yyyy-MM-dd)").toString(), Is.is("2012-07-20"));
    }

    @Test
    public void should_plus_days() throws ParseException {
        when(mockClock.afterDays(1)).thenReturn(new SimpleDateFormat("yyyy-MM-dd").parse("2012-07-21"));

        assertThat(filter.handle("DATE(1,yyyy-MM-dd)").toString(), Is.is("2012-07-21"));
    }

    @Test
    public void should_minus_days() throws ParseException {
        when(mockClock.afterDays(-1)).thenReturn(new SimpleDateFormat("yyyy-MM-dd").parse("2012-07-19"));

        assertThat(filter.handle("DATE(-1,yyyy-MM-dd)").toString(), Is.is("2012-07-19"));
    }


}
