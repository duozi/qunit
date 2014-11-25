/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.config;

import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.command.PreDataStepCommand;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.model.KeyValueStore;

import java.util.Arrays;


/**
 * 描述：
 * Created by JarnTang at 12-6-4 下午6:25
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class PreDataStepConfig extends StepConfig {

    public final static String FILE = "file";
    public final static String DATABASE = "database";
    public final static String REPLACETABLENAME = "replaceTableName";
    public final static String CACHED = "cached";

    @Property(value = "file", required = true)
    String file;

    @Property(defaultValue = "default")
    String database;

    @Property("replaceTableName")
    String replaceTableName;

    @Property("cached")
    String cached;

    @Override
    public StepCommand createCommand() {
        return new PreDataStepCommand(Arrays.asList(
                new KeyValueStore(FILE, file),
                new KeyValueStore(DATABASE, database),
                new KeyValueStore(REPLACETABLENAME, replaceTableName),
                new KeyValueStore(CACHED, cached)
        ));
    }
}
