/*
 *
 * The DbUnit Database Testing Framework
 * Copyright (C)2002-2004, DbUnit.org
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */

package com.qunar.base.qunit.database.datatype;

import org.dbunit.dataset.ITable;
import org.dbunit.dataset.datatype.AbstractDataType;
import org.dbunit.dataset.datatype.TypeCastException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Manuel Laflamme
 * @author Last changed by: $Author: changjiang.tang $
 * @version $Revision: 6525 $ $Date: 2012-06-01 13:43:20 +0800 (Fri, 01 Jun 2012) $
 * @since 1.0 (Feb 19, 2002)
 */
public class TimestampDataType extends AbstractDataType {
    public static final TimestampDataType TYPE = new TimestampDataType();

    /**
     * Logger for this class
     */
    private static final Logger logger = LoggerFactory.getLogger(TimestampDataType.class);

    private TimestampDataType() {
        super("TIMESTAMP", Types.TIMESTAMP, Timestamp.class, false);
    }

    ////////////////////////////////////////////////////////////////////////////
    // DataType class

    public Object typeCast(Object value) throws TypeCastException {
        logger.debug("typeCast(value={}) - start", value);

        if (value == null || value == ITable.NO_VALUE) {
            return null;
        }

        if (value instanceof java.sql.Timestamp) {
            return value;
        }

        if (value instanceof java.util.Date) {
            java.util.Date date = (java.util.Date) value;
            return new java.sql.Timestamp(date.getTime());
        }

        if (value instanceof Long) {
            Long date = (Long) value;
            return new java.sql.Timestamp(date.longValue());
        }

        if (value instanceof String) {
            String stringValue = (String) value;

            String[] patterns = {
                    "yyyy-MM-dd HH:mm:ss.SSS Z",
                    "yyyy-MM-dd HH:mm:ss.SSS",
                    "yyyy-MM-dd HH:mm:ss Z",
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd HH:mm Z",
                    "yyyy-MM-dd HH:mm",
                    "yyyy-MM-dd Z",
                    "yyyy-MM-dd"
            };

            for (int i = 0; i < patterns.length; ++i) {
                String p = patterns[i];
                try {
                    DateFormat df = new SimpleDateFormat(p);
                    Date date = df.parse(stringValue);
                    return new java.sql.Timestamp(date.getTime());
                } catch (ParseException e) {
                    if (i < patterns.length) continue;
                    throw new TypeCastException(value, this, e);
                }
            }
        }

        throw new TypeCastException(value, this);
    }

    public boolean isDateTime() {
        logger.debug("isDateTime() - start");

        return true;
    }

    public Object getSqlValue(int column, ResultSet resultSet)
            throws SQLException, TypeCastException {
        if (logger.isDebugEnabled())
            logger.debug("getSqlValue(column={}, resultSet={}) - start", new Integer(column), resultSet);

        Timestamp value = resultSet.getTimestamp(column);
        if (value == null || resultSet.wasNull()) {
            return null;
        }
        return value;
    }

    public void setSqlValue(Object value, int column, PreparedStatement statement)
            throws SQLException, TypeCastException {
        if (logger.isDebugEnabled())
            logger.debug("setSqlValue(value={}, column={}, statement={}) - start",
                    new Object[]{value, new Integer(column), statement});

        statement.setTimestamp(column, (java.sql.Timestamp) typeCast(value));
    }
}