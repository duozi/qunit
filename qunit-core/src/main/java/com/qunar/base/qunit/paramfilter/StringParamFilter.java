package com.qunar.base.qunit.paramfilter;

import org.apache.commons.lang.StringUtils;

import java.util.Random;

/**
 * User: zhaohuiyu
 * Date: 6/26/12
 * Time: 3:02 PM
 */
public class StringParamFilter extends ParamFilter {

    private static final String PREFIX = "str:";

    String base = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    Random random = new Random();

    @Override
    protected String doHandle(String param) {
        String countStr = param.substring(PREFIX.length());
        Integer count = Integer.valueOf(countStr);
        if (count <= 0) return StringUtils.EMPTY;
        StringBuilder sb = new StringBuilder(count);
        for (int i = 0; i < count; ++i) {
            sb.append(getRandomStr());
        }
        return sb.toString();
    }

    @Override
    protected boolean support(String param) {
        return param.startsWith(PREFIX);
    }

    protected char getRandomStr() {
        int index = random.nextInt(base.length());
        return base.charAt(index);
    }

}
