package com.qunar.base.qunit.paramfilter;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * User: zhaohuiyu
 * Date: 12/11/12
 */
public class TimestampParamFilter extends AbstractDateParamFilter {
    private static final Pattern pattern = Pattern.compile("DATE\\(\\s*([+-]?[0-9]+)\\s*,\\s*#([MmDdHhSs]+)\\s*,?\\s*([YyMDdHhmSs]?)\\s*\\)");

    public TimestampParamFilter(Clock clock) {
        super(clock);
    }

    @Override
    protected String postProcess(String param, String group1, String group2, String group3, String result) {
        return result;
    }

    @Override
    protected String format(String param, String formatExpression, Date date) {
        Long integer = TIME_MAP.get(formatExpression.toLowerCase());
        if (integer != null) {
            return String.valueOf(date.getTime() / integer);
        }
        return String.valueOf(date.getTime());
    }

    @Override
    protected Pattern getPattern() {
        return pattern;
    }

    private static final Map<String, Long> TIME_MAP = new HashMap<String, Long>();

    static {
        TIME_MAP.put("ms", 1L);
        TIME_MAP.put("s", TimeUnit.SECONDS.toMillis(1));
        TIME_MAP.put("m", TimeUnit.MINUTES.toMillis(1));
        TIME_MAP.put("h", TimeUnit.HOURS.toMillis(1));
        TIME_MAP.put("d", TimeUnit.DAYS.toMillis(1));
    }
}
