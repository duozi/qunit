package com.qunar.base.qunit.database.mysql;

import com.qunar.base.qunit.database.QunitDataTypeFactory;
import com.qunar.base.qunit.database.datatype.BitDataType;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.DataTypeException;

import java.sql.Types;

/**
 * User: zhaohuiyu
 * Date: 5/20/13
 * Time: 4:16 PM
 */
public class MysqlDataTypeFactory extends QunitDataTypeFactory {
    public DataType createDataType(int sqlType, String sqlTypeName) throws DataTypeException {
        if (sqlType != Types.OTHER) {
            if ("BIT".equals(sqlTypeName)) {
                return BitDataType.TYPE;
            }
            if ("INT UNSIGNED".equals(sqlTypeName)) {
                return DataType.BIGINT;
            }
            return super.createDataType(sqlType, sqlTypeName);
        } else {
            return super.createDataType(sqlType, sqlTypeName);
        }
    }
}
