package com.qunar.base.qunit.config;

import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.command.CompareDatabaseStepCommand;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.model.KeyValueStore;

import java.util.Arrays;

/**
 * User: zhaohuiyu
 * Date: 4/26/13
 * Time: 10:51 AM
 */
public class CompareDatabaseStepConfig extends StepConfig {

    public final static String NAME = "compareDatabase";

    public final static String DATABASE = "database";
    public final static String IGNORE = "ignore";
    public final static String EXPECTED = "expected";
    public final static String REPLACETABLENAME = "replaceTableName";
    public final static String ORDERBY = "orderBy";

    @Property(defaultValue = "default")
    private String database;

    //tb1;tb2(col1,col2);
    @Property
    private String ignore;

    @Property
    private String expected;

    @Property("replaceTableName")
    private String replaceTableName;

    @Property
    private String orderBy;

    @Override
    public StepCommand createCommand() {
        return new CompareDatabaseStepCommand(Arrays.asList(
                new KeyValueStore(DATABASE, database),
                new KeyValueStore(IGNORE, ignore),
                new KeyValueStore(EXPECTED, expected),
                new KeyValueStore(REPLACETABLENAME, replaceTableName),
                new KeyValueStore(ORDERBY, orderBy)
        ));
    }
}
