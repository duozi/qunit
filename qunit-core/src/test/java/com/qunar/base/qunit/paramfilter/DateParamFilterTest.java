package com.qunar.base.qunit.paramfilter;

import org.apache.commons.lang.time.DateUtils;
import org.hamcrest.core.Is;
import org.junit.Before;
import org.junit.Test;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: zhaohuiyu
 * Date: 7/20/12
 * Time: 5:44 PM
 */
public class DateParamFilterTest {

    private DateParamFilter filter;
    private Date today;

    @Before
    public void setUp() throws ParseException {
        Clock clock = mock(Clock.class);
        today = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2013-08-02 10:10:10");
        when(clock.current()).thenReturn(today);

        filter = new DateParamFilter(clock);
    }

    @Test
    public void should_return_now() throws ParseException {
        String expression = "yyyy-MM-dd";
        String expected = new SimpleDateFormat(expression).format(today);

        assertThat(filter.handle("DATE(0," + expression + ")").toString(), Is.is(expected));
    }

    @Test
    public void should_parse_date_even_though_with_empty_space() throws ParseException {
        String expression = "yyyy-MM-dd";
        String expected = new SimpleDateFormat(expression).format(today);

        assertThat(filter.handle("DATE(0,  " + expression + " )").toString(), Is.is(expected));
    }

    @Test
    public void should_get_tomorrow_in_given_format() throws ParseException {
        String expression = "yyyy-MM-dd";
        Date tomorrow = DateUtils.addDays(today, 1);
        String expected = new SimpleDateFormat(expression).format(tomorrow);

        assertThat(filter.handle("DATE(1," + expression + ")").toString(), Is.is(expected));
    }

    @Test
    public void should_get_yesterday_in_given_format() throws ParseException {
        String expression = "yyyy-MM-dd";
        Date yesterday = DateUtils.addDays(today, -1);
        String expected = new SimpleDateFormat(expression).format(yesterday);

        assertThat(filter.handle("DATE(-1," + expression + ")").toString(), Is.is(expected));
    }

    @Test
    public void should_get_last_20_hours_in_given_format() {
        String expression = "yyyy-MM-dd";
        Date last20Hours_yesterday = DateUtils.addHours(today, -20);
        String expected = new SimpleDateFormat(expression).format(last20Hours_yesterday);

        assertThat(filter.handle("DATE(-20, " + expression + " , h )").toString(), Is.is(expected));
    }
}
