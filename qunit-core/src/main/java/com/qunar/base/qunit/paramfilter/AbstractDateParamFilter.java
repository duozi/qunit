package com.qunar.base.qunit.paramfilter;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.time.DateUtils;

import java.util.Date;
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
