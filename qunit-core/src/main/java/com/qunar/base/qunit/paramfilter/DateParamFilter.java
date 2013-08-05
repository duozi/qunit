package com.qunar.base.qunit.paramfilter;

import org.apache.commons.lang.StringUtils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: zhaohuiyu
 * Date: 7/20/12
 * Time: 5:16 PM
 */
public class DateParamFilter extends ParamFilter {

    private Clock clock;

    public DateParamFilter(Clock clock) {
        this.clock = clock;
    }

    //DATE(0,yyyy-MM-dd) -> today
    //DATE(1,yyyy-MM-dd) -> tomorrow
    //DATE(-1,yyyy-MM-dd) -> yesterday
    private final static Pattern pattern = Pattern.compile("DATE\\(([+-]?[0-9]+),([^\\)]*)\\)");

    @Override
    protected String doHandle(String param) {
        String result = param;
        Matcher matcher = pattern.matcher(param);
        while (matcher.find()) {
            String group1 = matcher.group(1);
            String diff = StringUtils.trim(group1);
            String group2 = matcher.group(2);
            String format = StringUtils.trim(group2);
            Integer i = Integer.valueOf(diff);
            Date date = clock.afterDays(i);
            result = result.replace("DATE(" + group1 + "," + group2 + ")", new SimpleDateFormat(format).format(date));
        }
        return result;
    }

    @Override
    protected boolean support(String param) {
        return !StringUtils.isBlank(param) && pattern.matcher(param).find();
    }
}
