package com.qunar.base.qunit.paramfilter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: zhaohuiyu
 * Date: 8/5/13
 * Time: 11:15 AM
 */
public abstract class AbstractDateParamFilter extends ParamFilter {
    private Clock clock;

    public AbstractDateParamFilter(Clock clock) {
        this.clock = clock;
    }

    protected abstract Pattern getPattern();

    @Override
    protected String doHandle(String param) {
        String result = param;
        Matcher matcher = getPattern().matcher(param);
        while (matcher.find()) {
            String group1 = matcher.group(1);
            String diff = StringUtils.trim(group1);
            if (StringUtils.isNotBlank(diff) && diff.startsWith("+")) {
                diff = diff.substring(1);
            }

            String group2 = matcher.group(2);
            String formatExpression = StringUtils.trim(group2);

            String group3 = matcher.group(3);
            Step step = getStep(group3);

            Integer i = Integer.valueOf(diff);
            Date date = step.diff(i);

            String formatDate = format(param, formatExpression, date);
            result = postProcess(result, group1, group2, group3, formatDate);
        }
        return result;
    }

    protected abstract String postProcess(String param, String group1, String group2, String group3, String result);

    protected abstract String format(String param, String formatExpression, Date date);

    protected Step getStep(String step) {
        step = StringUtils.trim(step);

        if (StringUtils.isBlank(step)) return new DayStep();
        switch (step.charAt(0)) {
            case 'Y':
            case 'y':
                return new YearStep();
            case 'M':
                return new MonthStep();
            case 'D':
            case 'd':
                return new DayStep();
            case 'H':
            case 'h':
                return new HourStep();
            case 'm':
                return new MinuteStep();
            case 'S':
            case 's':
                return new SecondsStep();
            default:
                return new DayStep();
        }
    }

    protected abstract class Step {
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

    @Override
    protected boolean support(String param) {
        return !StringUtils.isBlank(param) && getPattern().matcher(param).find();
    }
}
