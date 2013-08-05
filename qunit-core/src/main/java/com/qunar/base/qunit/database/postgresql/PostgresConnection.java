package com.qunar.base.qunit.database.postgresql;

import org.dbunit.DatabaseUnitException;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.database.DatabaseConnection;

import java.sql.Connection;

/**
 * User: zhaohuiyu
 * Date: 12/22/12
 */
public class PostgresConnection extends DatabaseConnection {
    public PostgresConnection(Connection connection, String schema) throws DatabaseUnitException {
        super(connection, schema);
        PostgresqlDataTypeFactory dataTypeFactory = new PostgresqlDataTypeFactory();
        getConfig().setProperty(DatabaseConfig.PROPERTY_DATATYPE_FACTORY, dataTypeFactory);
        getConfig().setProperty(DatabaseConfig.PROPERTY_ESCAPE_PATTERN, "\"");
    }
}
