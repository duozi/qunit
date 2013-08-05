/*
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.database;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

public class SqlRunner {
    private final static Logger logger = LoggerFactory.getLogger(SqlRunner.class);

    private DataSource dataSource;

    public SqlRunner(String database) {
        dataSource = new PooledDataSource(database);
    }

    public List<Map<String, Object>> execute(String sql) throws Exception {
        Connection connection = null;
        try {
            connection = dataSource.getConnection();
            return execute(connection, sql);
        } finally {
            close(connection);
        }
    }

    private List<Map<String, Object>> execute(Connection connection, String sql) throws SQLException {
        sql = sql.trim();
        if (isQuery(sql)) {
            return query(connection, sql);
        } else {
            return executeBatch(connection, sql);
        }
    }

    private List<Map<String, Object>> executeBatch(Connection connection, String sql) throws SQLException {
        Statement statement = null;
        try {
            statement = connection.createStatement();
            String[] sqlList = StringUtils.split(sql, ";");
            for (String singleSql : sqlList) {
                statement.addBatch(singleSql);
            }
            Map<String, Object> temp = new HashMap<String, Object>();
            temp.put("result", statement.executeBatch());
            return Arrays.asList(temp);
        } finally {
            close(statement);
        }
    }

    private List<Map<String, Object>> query(Connection connection, String sql) throws SQLException {
        PreparedStatement statement = null;
        try {
            statement = connection.prepareStatement(sql);
            List<Map<String, Object>> result = new ArrayList<Map<String, Object>>();
            ResultSet resultSet = statement.executeQuery();
            int columnCount = resultSet.getMetaData().getColumnCount();
            while (resultSet.next()) {
                result.add(extractRow(resultSet, columnCount));
            }
            close(resultSet);
            return result;
        } finally {
            close(statement);
        }
    }

    private boolean isQuery(String sql) {
        return sql.toLowerCase().startsWith("select");
    }

    private Map<String, Object> extractRow(ResultSet resultSet, int columnCount) throws SQLException {
        Map<String, Object> result = new HashMap<String, Object>();
        for (int i = 1; i <= columnCount; i++) {
            result.put(resultSet.getMetaData().getColumnLabel(i), resultSet.getString(i));
        }
        return result;
    }

    private void close(ResultSet resultSet) {
        if (resultSet != null) {
            try {
                resultSet.close();
            } catch (SQLException e) {
                logger.error("ResultSet close error", e);
            }
        }
    }

    private void close(Connection connection) {
        if (connection != null) {
            try {
                connection.close();
            } catch (SQLException e) {
                logger.error("Connection close error", e);
            }
        }
    }

    private void close(Statement statement) {
        if (statement == null) return;
        try {
            statement.close();
        } catch (SQLException e) {
            logger.error(e.getMessage(), e);
        }
    }
}
