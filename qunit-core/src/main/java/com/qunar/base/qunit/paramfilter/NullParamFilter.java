package com.qunar.base.qunit.paramfilter;

/**
 * User: zhaohuiyu
 * Date: 6/26/12
 * Time: 3:26 PM
 */
public class NullParamFilter extends ParamFilter {
    @Override
    protected String doHandle(String param) {
        return null;
    }

    @Override
    protected boolean support(String param) {
        return param.equals("NULL") || param.equals("[NULL]");
    }

}
