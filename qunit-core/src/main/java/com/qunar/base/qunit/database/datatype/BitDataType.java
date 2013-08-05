package com.qunar.base.qunit.database.datatype;

import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.datatype.TypeCastException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;

/**
 * User: zhaohuiyu
 * Date: 5/20/13
 * Time: 3:28 PM
 */
public class BitDataType extends AbstractDataType {

    public static final BitDataType TYPE = new BitDataType();

    private static final Logger logger = LoggerFactory.getLogger(BitDataType.class);

    private BitDataType() {
        super("BIT", Types.BIT, Integer.class, false);
    }

    @Override
    public Object typeCast(Object value) throws TypeCastException {
        logger.debug("typeCast(value={}) - start", value);

        if (value == null || value == ITable.NO_VALUE) {
            return null;
        }

        if (value instanceof Boolean) {
            if (value.equals(Boolean.TRUE)) return 1;
            else return 0;
        }

        if (value instanceof Number) {
            Number number = (Number) value;
            return number.intValue();
        }

        if (value instanceof String) {
            String string = (String) value;

            if (string.equalsIgnoreCase("true")) {
                return 1;
            } else if (string.equalsIgnoreCase("false")) {
                return 0;
            } else {
                return typeCast(DataType.INTEGER.typeCast(string));
            }
        }

        throw new TypeCastException(value, this);
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement)
            throws SQLException, TypeCastException {
        if (logger.isDebugEnabled())
            logger.debug("setSqlValue(value={}, column={}, statement={}) - start",
                    new Object[]{value, new Integer(column), statement});

        Object targetValue = typeCast(value);

        if (targetValue == null) {
            statement.setNull(column, Types.TINYINT);
        } else {
            Integer intValue = (Integer) targetValue;
            statement.setInt(column, intValue);
        }
    }
}
