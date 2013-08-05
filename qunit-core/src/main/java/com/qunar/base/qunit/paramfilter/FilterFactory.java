package com.qunar.base.qunit.paramfilter;

import java.util.ArrayList;
import java.util.List;

/**
 * User: zhaohuiyu
 * Date: 6/26/12
 * Time: 11:13 AM
 */
public class FilterFactory {
    private static final List<ParamFilter> FILTERS = new ArrayList<ParamFilter>();

    static {
        register(new StringParamFilter());
        register(new NullParamFilter());
        Clock clock = new Clock();
        register(new TimestampParamFilter(clock));
        register(new DateParamFilter(clock));
        register(new EmptyParamFilter());
        register(new SpecialCharFilter());
        register(new FileFilter());
    }

    public static void register(ParamFilter filter) {
        if (!FILTERS.contains(filter)) {
            FILTERS.add(filter);
        }
    }

    public static Object handle(String param) {
        Object result = param;
        for (ParamFilter filter : FILTERS) {
            if (result instanceof String) {
                param = (String) result;
                result = filter.handle(param);
            }
        }
        return result;
    }
}
