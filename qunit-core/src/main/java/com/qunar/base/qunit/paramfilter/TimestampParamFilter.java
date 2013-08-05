package com.qunar.base.qunit.paramfilter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

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
public class TimestampParamFilter extends ParamFilter {
    private Pattern pattern = Pattern.compile("DATE\\(\\s*([+-]?[0-9]+)\\s*,\\s*#([MmDdHhSs]+)\\s*,?\\s*([YyMDdHhmSs]?)\\s*\\)");

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
    protected boolean support(String param) {
        return !StringUtils.isBlank(param) && pattern.matcher(param).find();
    }

    private static final Map<String, Long> TIME_MAP = new HashMap<String, Long>();

    static {
        TIME_MAP.put("ms", 1L);
        TIME_MAP.put("s", TimeUnit.SECONDS.toMillis(1));
        TIME_MAP.put("m", TimeUnit.MINUTES.toMillis(1));
        TIME_MAP.put("h", TimeUnit.HOURS.toMillis(1));
        TIME_MAP.put("d", TimeUnit.DAYS.toMillis(1));
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
