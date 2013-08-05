package com.qunar.base.qunit.paramfilter;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: zhaohuiyu
 * Date: 7/20/12
 * Time: 5:16 PM
 */
public class DateParamFilter extends AbstractDateParamFilter {

    public DateParamFilter(Clock clock) {
        super(clock);
    }

    //DATE(0,yyyy-MM-dd) -> today
    //DATE(1,yyyy-MM-dd) -> tomorrow
    //DATE(-1,yyyy-MM-dd) -> yesterday
    private static final Pattern pattern = Pattern.compile("DATE\\((\\s*[+-]?[0-9]+\\s*),(\\s*[^\\),]*\\s*),?(\\s*[YyMDdHhmSs]?\\s*)\\)");

    @Override
    protected String doHandle(String param) {
        String result = param;
        Matcher matcher = pattern.matcher(param);
        while (matcher.find()) {
            String group1 = matcher.group(1);
            String diff = StringUtils.trim(group1);

            String group2 = matcher.group(2);
            DateFormat format = getDateFormat(param, group2);

            String group3 = matcher.group(3);
            Step step = getStep(group3);

            Integer i = Integer.valueOf(diff);
            Date date = step.diff(i);

            if (StringUtils.isBlank(group3)) {
                result = result.replace("DATE(" + group1 + "," + group2 + ")", format.format(date));
            } else {
                result = result.replace("DATE(" + group1 + "," + group2 + "," + group3 + ")", format.format(date));
            }
        }
        return result;
    }

    private DateFormat getDateFormat(String param, String format) {
        format = StringUtils.trim(format);
        if (StringUtils.isBlank(format)) throw new RuntimeException("时间格式化串非法 " + format + " in " + param);
        try {
            return new SimpleDateFormat(format);
        } catch (Exception e) {
            throw new RuntimeException("时间格式化串非法 " + format + " in " + param);
        }
    }

    @Override
    protected Pattern getPattern() {
        return pattern;
    }
}
