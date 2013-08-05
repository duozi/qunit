package com.qunar.base.qunit.paramfilter;

import org.apache.commons.lang.time.DateUtils;
import org.junit.Before;
import org.junit.Test;

import java.text.SimpleDateFormat;
import java.util.Date;

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * User: zhaohuiyu
 * Date: 8/2/13
 * Time: 10:40 AM
 */
public class TimestampParamFilterTest {

    private Clock clock;
    private Date today;
    private TimestampParamFilter filter;

    @Before
    public void setUp() throws Exception {
        clock = mock(Clock.class);
        today = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss").parse("2013-08-02 10:10:10");
        when(clock.current()).thenReturn(today);
        filter = new TimestampParamFilter(clock);
    }

    @Test
    public void should_get_today_in_minutes() {
        Object result = filter.handle("DATE(0,#m)");

        Long expected = today.getTime() / (60 * 1000);
        assertThat(result.toString(), is(expected.toString()));
    }

    @Test
    public void should_get_yesterday_in_minutes() {
        Object result = filter.handle("DATE(-1,#m)");

        Date yesterday = DateUtils.addDays(today, -1);
        Long expected = yesterday.getTime() / (60 * 1000);
        assertThat(result.toString(), is(expected.toString()));
    }

    @Test
    public void should_get_last_10_minutes_in_seconds() {
        Object result = filter.handle("DATE(-10,#s,m)");

        Date last10Minutes = DateUtils.addMinutes(today, -10);
        Long expected = last10Minutes.getTime() / 1000;
        assertThat(result.toString(), is(expected.toString()));
    }

    @Test
    public void should_get_last_10_minutes_in_seconds_format_output() {
        String expectedFormat = "yyyy-MM-dd hh:mm:ss";
        Object result = filter.handle("DATE(-10,#s,m," + expectedFormat + ")");

        Date last10Minutes = DateUtils.addMinutes(today, -10);
        String expected = new SimpleDateFormat(expectedFormat).format(last10Minutes);
        assertThat(result.toString(), is(expected));
    }
}
