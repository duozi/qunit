/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.command;

import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.exception.ExecuteException;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;

import java.util.*;

import static com.qunar.base.qunit.config.SqlStepConfig.DATABASE;
import static com.qunar.base.qunit.config.SqlStepConfig.SQL;

/**
 * This class is Deprecated, please use sql + assert.
 */
@Deprecated
public class DbAssertStepCommand extends StepCommand {

    String sql;
    String expect;

    public DbAssertStepCommand(String sql, String expect) {
        this.sql = sql;
        this.expect = expect;
    }

    @Override
    public Response doExecute(Response preResult, Context context) throws Throwable {
        try {
            logger.info("db assert command<sql={}, expect={}> is starting...", sql, expect);
            List<KeyValueStore> params = Arrays.asList(new KeyValueStore(SQL, sql), new KeyValueStore(DATABASE, "default"));
            SqlStepCommand sqlStepCommand = new SqlStepCommand(params);
            Response response = sqlStepCommand.doExecute(preResult, null);
            AssertStepCommand assertStepCommand = new AssertStepCommand(Arrays.asList(new KeyValueStore("body", this.expect)));
            assertStepCommand.doExecute(response, context);
            logger.info("db assert command<sql={}, expect={}> is finished, result={}", new Object[]{sql, expect, response.getBody().toString()});
            return preResult;
        } catch (Exception e) {
            String message = String.format("db assert step command invoke error,sql=<%s>, expect=<%s>", sql, expect);
            logger.error(message, e);
            throw new ExecuteException(message, e);
        }
    }

    @Override
    public StepCommand doClone() {
        return new DbAssertStepCommand(sql, expect);
    }

    @Override
    public Map<String, Object> toReport() {
        Map<String, Object> details = new HashMap<String, Object>();
        details.put("stepName", "执行:");
        details.put("name", "数据库验证(该命令已不建议使用，请使用sql和assert代替)");
        List<KeyValueStore> params = new ArrayList<KeyValueStore>();
        params.add(new KeyValueStore("SQL", this.sql));
        params.add(new KeyValueStore("期望", this.expect));
        details.put("params", params);
        return details;
    }

}
