package com.qunar.base.qunit.paramfilter;

import org.apache.commons.lang.StringUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
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
    protected String postProcess(String param, String group1, String group2, String group3, String result) {
        if (StringUtils.isBlank(group3)) {
            result = param.replace("DATE(" + group1 + "," + group2 + ")", result);
        } else {
            result = param.replace("DATE(" + group1 + "," + group2 + "," + group3 + ")", result);
        }
        return result;
    }

    @Override
    protected String format(String param, String formatExpression, Date date) {
        DateFormat dateFormat = getDateFormat(param, formatExpression);
        return dateFormat.format(date);
    }

    private DateFormat getDateFormat(String param, String formatExpression) {
        if (StringUtils.isBlank(formatExpression))
            throw new RuntimeException("时间格式化串非法 " + formatExpression + " in " + param);
        try {
            return new SimpleDateFormat(formatExpression);
        } catch (Exception e) {
            throw new RuntimeException("时间格式化串非法 " + formatExpression + " in " + param);
        }
    }

    @Override
    protected Pattern getPattern() {
        return pattern;
    }
}
