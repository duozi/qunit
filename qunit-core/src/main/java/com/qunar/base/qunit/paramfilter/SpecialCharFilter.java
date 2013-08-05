/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.paramfilter;

import static com.qunar.base.qunit.util.PropertyUtils.getProperty;

/**
 * 描述：
 * Created by JarnTang at 12-8-8 上午11:38
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class SpecialCharFilter extends ParamFilter{

    private static final String SPECIAL_CHAR = "!@#$%^&*()_+-<>?/'|\"\\{}[]~`";

    String specialChar;

    public SpecialCharFilter(){
        specialChar = getProperty("specialChar", SPECIAL_CHAR);
        if (!SPECIAL_CHAR.equals(specialChar)) {
            logger.info(String.format("load special char [%s] from config file.", specialChar));
        }
    }

    @Override
    protected String doHandle(String param) {
        return specialChar;
    }

    @Override
    protected boolean support(String param) {
        return "[SPECIAL]".equals(param);
    }

}
