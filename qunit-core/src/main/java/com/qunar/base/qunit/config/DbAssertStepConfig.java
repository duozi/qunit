/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.config;

import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.command.DbAssertStepCommand;
import com.qunar.base.qunit.command.StepCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Deprecated replace with sql + assert
 */
@Deprecated
public class DbAssertStepConfig extends StepConfig {
    private final static Logger logger = LoggerFactory.getLogger(DbAssertStepConfig.class);

    @Property
    String sql;

    @Property
    String expect;

    @Override
    public StepCommand createCommand() {
        logger.warn("<dbassert>已不推荐使用，请使用<sql> + <assert>替代");
        return new DbAssertStepCommand(sql, expect);
    }

}
