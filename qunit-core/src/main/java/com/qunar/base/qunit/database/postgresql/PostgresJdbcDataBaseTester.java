package com.qunar.base.qunit.database.postgresql;

import org.dbunit.AbstractDatabaseTester;
import org.dbunit.database.IDatabaseConnection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;


public class PostgresJdbcDataBaseTester extends AbstractDatabaseTester {
    private final static Logger logger = LoggerFactory.getLogger(PostgresJdbcDataBaseTester.class);

    private DataSource dataSource;

    public PostgresJdbcDataBaseTester(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public synchronized IDatabaseConnection getConnection() throws Exception {
        logger.debug("getConnection() - start");
        return new PostgresConnection(dataSource.getConnection(), getSchema());
    }

}
