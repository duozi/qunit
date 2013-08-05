package com.qunar.base.qunit.paramfilter;

import com.google.common.base.Strings;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: zhaohuiyu
 * Date: 12/11/12
 */
public class TimestampParamFilter extends ParamFilter {
    private Pattern pattern = Pattern.compile("DATE\\((\\s*[+-]?[0-9]+\\s*),\\s*#([YyMmDdHhSsNnUuPp]+\\s*),?(\\s*[YMDHmS]?\\s*),?([^\\)]*)\\)");

    private final Clock clock;

    public TimestampParamFilter(Clock clock) {
        this.clock = clock;
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

            String group4 = matcher.group(4);
            DateFormat format = getFormat(group4);

            Integer i = Integer.valueOf(diff);
            Date date = step.diff(i);
            if (format != null) return format.format(date);
            Integer integer = TIME_MAP.get(numberSuffix.toLowerCase());
            if (integer != null) {
                return date.getTime() / integer;
            }
            return date.getTime();
        }
        return expression;
    }

    private DateFormat getFormat(String format) {
        if (Strings.isNullOrEmpty(format)) return null;
        return new SimpleDateFormat(format);
    }

    @Override
    protected boolean support(String param) {
        return !StringUtils.isBlank(param) && pattern.matcher(param).find();
    }

    private static final Map<String, Integer> TIME_MAP = new HashMap<String, Integer>();

    static {
        TIME_MAP.put("ms", 1);
        TIME_MAP.put("s", 1000);
        TIME_MAP.put("m", 60 * 1000);
        TIME_MAP.put("h", 60 * 1000);
    }

    private Step getStep(String step) {
        if (StringUtils.isBlank(step)) return new DayStep();
        switch (step.charAt(0)) {
            case 'Y':
                return new YearStep();
            case 'M':
                return new MonthStep();
            case 'D':
                return new DayStep();
            case 'H':
                return new HourStep();
            case 'm':
                return new MinuteStep();
            case 'S':
                return new SecondsStep();
            default:
                return new DayStep();
        }
    }

    private abstract class Step {
        public abstract Date diff(Integer i);
    }

    private class DayStep extends Step {
        public Date diff(Integer i) {
            return DateUtils.addDays(clock.current(), i);
        }
    }

    private class YearStep extends Step {
        public Date diff(Integer i) {
            return DateUtils.addYears(clock.current(), i);
        }
    }

    private class MonthStep extends Step {
        public Date diff(Integer i) {
            return DateUtils.addMonths(clock.current(), i);
        }
    }

    private class HourStep extends Step {
        public Date diff(Integer i) {
            return DateUtils.addHours(clock.current(), i);
        }
    }

    private class MinuteStep extends Step {
        public Date diff(Integer i) {
            return DateUtils.addMinutes(clock.current(), i);
        }
    }

    private class SecondsStep extends Step {
        public Date diff(Integer i) {
            return DateUtils.addSeconds(clock.current(), i);
        }
    }
}
