package com.qunar.base.qunit.paramfilter;

import org.apache.commons.lang.StringUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
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
    public Object doHandle(String expression) {
        Matcher matcher = this.pattern.matcher(expression);
        if (matcher.find()) {
            String group1 = matcher.group(1);
            String diff = StringUtils.trim(group1);

            String group2 = matcher.group(2);
            String numberSuffix = StringUtils.trim(group2);

            String group3 = matcher.group(3);
            Step step = getStep(group3);

            Integer i = Integer.valueOf(diff);
            Date date = step.diff(i);
            Long integer = TIME_MAP.get(numberSuffix.toLowerCase());
            if (integer != null) {
                return date.getTime() / integer;
            }
            return date.getTime();
        }
        return expression;
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
