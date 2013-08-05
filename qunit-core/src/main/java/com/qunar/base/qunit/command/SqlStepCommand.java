/*
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */

package com.qunar.base.qunit.command;

import com.alibaba.fastjson.JSON;
import com.qunar.base.qunit.config.SqlStepConfig;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.database.SqlRunner;
import com.qunar.base.qunit.exception.ExecuteException;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;
import com.qunar.base.qunit.util.CloneUtil;
import com.qunar.base.qunit.util.KeyValueUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.alibaba.fastjson.serializer.SerializerFeature.WriteMapNullValue;

/**
 * desc
 * Created by JarnTang at 12-8-14 下午5:23
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class SqlStepCommand extends ParameterizedCommand {

    public SqlStepCommand(List<KeyValueStore> params) {
        super(params);
    }

    @Override
    protected Response doExecuteInternal(Response preResult, List<KeyValueStore> processedParams, Context context) throws Throwable {
        String sql = getSql(processedParams);
        String database = getDatabase(processedParams);
        try {
            logger.info("sql command<sql={}> is starting...", sql);
            SqlRunner sqlRunner = getDbUtil(database);
            List<Map<String, Object>> query = sqlRunner.execute(sql);
            String resultJson = getExpectJson(query);
            return new Response(resultJson, null);
        } catch (Exception e) {
            String message = String.format("sql step command invoke error,sql=<%s>", sql);
            logger.error(message, e);
            throw new ExecuteException(message, e);
        }
    }

    private String getSql(List<KeyValueStore> processedParams) {
        return KeyValueUtil.getValueByKey(SqlStepConfig.SQL, processedParams);
    }

    private String getDatabase(List<KeyValueStore> processedParams) {
        return KeyValueUtil.getValueByKey(SqlStepConfig.DATABASE, processedParams);
    }


    @Override
    public StepCommand doClone() {
        return new SqlStepCommand(CloneUtil.cloneKeyValueStore(params));
    }

    protected SqlRunner getDbUtil(String database) {
        return new SqlRunner(database);
    }

    private String getExpectJson(List<Map<String, Object>> query) {
        String expectJson;
        if (query == null || query.size() == 0) {
            return JSON.toJSONString(null);
        }
        if (query.size() == 1) {
            expectJson = JSON.toJSONString(query.get(0), WriteMapNullValue);
        } else {
            expectJson = JSON.toJSONString(query, WriteMapNullValue);
        }
        return expectJson;
    }

    @Override
    public Map<String, Object> toReport() {
        Map<String, Object> details = new HashMap<String, Object>();
        details.put("stepName", String.format("在数据库 %s 上执行 ", getDatabase(params)));
        details.put("name", String.format("SQL: %s", getSql(params)));
        List<KeyValueStore> params = new ArrayList<KeyValueStore>();
        details.put("params", params);
        return details;
    }

}
