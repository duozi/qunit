package com.qunar.base.qunit.database;

import com.qunar.base.qunit.database.datatype.TimestampDataType;
import com.qunar.base.qunit.database.postgresql.ArrayDataType;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;
import org.dbunit.dataset.datatype.DefaultDataTypeFactory;

import java.sql.Types;
import java.util.Arrays;
import java.util.Collection;

import static org.dbunit.dataset.datatype.DataType.*;

/**
 * User: zhaohuiyu
 * Date: 12/24/12
 * Time: 2:30 PM
 */
public class QunitDataTypeFactory extends DefaultDataTypeFactory {

    private static final DataType[] TYPES = {
            VARCHAR, CHAR, LONGVARCHAR, NCHAR, NVARCHAR, LONGNVARCHAR, CLOB, NUMERIC, DECIMAL, BOOLEAN, BIT, INTEGER,
            TINYINT, SMALLINT, BIGINT, REAL, DOUBLE, FLOAT, DATE, TIME, TIMESTAMP,
            VARBINARY, BINARY, LONGVARBINARY, BLOB,
            //auxiliary types at the very end
            BIGINT_AUX_LONG
    };

    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
        if (sqlType == Types.TIMESTAMP) {
            return TimestampDataType.TYPE;
        }

        DataType dataType = DataType.UNKNOWN;
        if (sqlType != Types.OTHER) {
            dataType = forSqlType(sqlType, sqlTypeName);
        } else {
            // Necessary for compatibility with DbUnit 1.5 and older
            // BLOB
            if ("BLOB".equals(sqlTypeName)) {
                return DataType.BLOB;
            }

            // CLOB
            if ("CLOB".equals(sqlTypeName)) {
                return DataType.CLOB;
            }
        }
        return dataType;
    }

    protected DataType forSqlType(int sqlType, String sqlTypeName) throws DataTypeException {
        for (int i = 0; i < TYPES.length; i++) {
            if (sqlType == TYPES[i].getSqlType()) {
                return TYPES[i];
            } else if (sqlType == 2003) {
                return new ArrayDataType(sqlTypeName, sqlType);
            }
        }

        return UNKNOWN;
    }

    private static final Collection DATABASE_PRODUCTS = Arrays.asList("mysql");

    @Override
    public Collection getValidDbProducts() {
        return DATABASE_PRODUCTS;
    }
}
