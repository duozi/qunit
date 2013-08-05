package com.qunar.base.qunit.config;

import com.qunar.base.qunit.annotation.ConfigElement;
import com.qunar.base.qunit.annotation.Property;
import com.qunar.base.qunit.command.SqlStepCommand;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.model.KeyValueStore;

import java.util.Arrays;

/**
 * Date: 12-6-11
 * Time: 下午12:19
 */
@ConfigElement(defaultProperty = "sql")
public class SqlStepConfig extends StepConfig {

    public static final String SQL = "sql";
    public static final String DATABASE = "database";

    @Property(required = true)
    String sql;

    @Property(defaultValue = "default")
    String database;

    @Override
    public StepCommand createCommand() {
        return new SqlStepCommand(Arrays.asList(new KeyValueStore(SQL, sql), new KeyValueStore(DATABASE, database)));
    }

}
