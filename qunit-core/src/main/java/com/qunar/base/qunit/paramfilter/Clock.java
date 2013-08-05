package com.qunar.base.qunit.paramfilter;

import org.apache.commons.lang.time.DateUtils;

import java.util.Date;

/**
 * User: zhaohuiyu
 * Date: 7/20/12
 * Time: 5:46 PM
 */
public class Clock {
    public Date afterDays(Integer diff) {
        return DateUtils.addDays(new Date(), diff);
    }

    public Date current() {
        return new Date();
    }
}
