package com.qunar.base.qunit.database.mysql;

import org.dbunit.AbstractDatabaseTester;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.ext.mysql.MySqlConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;

/**
 * User: zhaohuiyu
 * Date: 12/14/12
 */
public class MySqlJdbcDataBaseTester extends AbstractDatabaseTester {
    private final static Logger logger = LoggerFactory.getLogger(MySqlJdbcDataBaseTester.class);

    private DataSource dataSource;

    public MySqlJdbcDataBaseTester(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public IDatabaseConnection getConnection() throws Exception {
        logger.debug("getConnection() - start");
        MySqlConnection connection = new MySqlConnection(dataSource.getConnection(), getSchema());
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, new MysqlDataTypeFactory());
        connection.getConfig().setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN, "`");
        return connection;
    }
}
