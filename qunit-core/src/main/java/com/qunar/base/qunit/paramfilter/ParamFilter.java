package com.qunar.base.qunit.paramfilter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: zhaohuiyu
 * Date: 6/26/12
 * Time: 11:04 AM
 */
public abstract class ParamFilter {
    protected Logger logger = LoggerFactory.getLogger(getClass());

    public Object handle(String param) {
        if (param == null) return null;
        if (support(param)) {
            return doHandle(param);
        }
        return param;
    }

    protected abstract Object doHandle(String param);

    protected abstract boolean support(String param);

}
