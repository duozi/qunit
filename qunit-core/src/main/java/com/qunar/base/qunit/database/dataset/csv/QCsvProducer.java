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

package com.qunar.base.qunit.database.dataset.csv;

import org.dbunit.dataset.Column;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.DefaultTableMetaData;
import org.dbunit.dataset.ITableMetaData;
import org.dbunit.dataset.common.handlers.IllegalInputCharacterException;
import org.dbunit.dataset.common.handlers.PipelineException;
import org.dbunit.dataset.csv.CsvDataSetWriter;
import org.dbunit.dataset.csv.CsvParser;
import org.dbunit.dataset.csv.CsvParserException;
import org.dbunit.dataset.csv.CsvParserImpl;
import org.dbunit.dataset.datatype.DataType;
import org.dbunit.dataset.stream.DefaultConsumer;
import org.dbunit.dataset.stream.IDataSetConsumer;
import org.dbunit.dataset.stream.IDataSetProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.List;

/**
 * @author Federico Spinazzi
 * @author Last changed by: $Author: changjiang.tang $
 * @version $Revision: 8998 $ $Date: 2012-11-01 16:09:28 +0800 (Thu, 01 Nov 2012) $
 * @since 1.5 (Sep 17, 2003)
 */
public class QCsvProducer implements IDataSetProducer {

    private static final Logger logger = LoggerFactory.getLogger(QCsvProducer.class);

    private static final IDataSetConsumer EMPTY_CONSUMER = new DefaultConsumer();
    private IDataSetConsumer _consumer = EMPTY_CONSUMER;
    private String filePath;

    public QCsvProducer(String path) {
        filePath = path;
    }

    public void setConsumer(IDataSetConsumer consumer) throws DataSetException {
        logger.debug("setConsumer(consumer) - start");
        _consumer = consumer;
    }

    public void produce() throws DataSetException {
        logger.debug("produce() - start");
        File file = new File(filePath);
        _consumer.startDataSet();
        try {
            produceFromFile(file);
        } catch (CsvParserException e) {
            throw new DataSetException("error producing dataset for table '" + file.getName() + "'", e);
        } catch (DataSetException e) {
            throw new DataSetException("error producing dataset for table '" + file.getName() + "'", e);
        }
        _consumer.endDataSet();
    }

    private void produceFromFile(File theDataFile) throws DataSetException, CsvParserException {
        logger.debug("produceFromFile(theDataFile={}) - start", theDataFile);

        try {
            CsvParser parser = new CsvParserImpl();
            List readData = parser.parse(theDataFile);
            List readColumns = ((List) readData.get(0));
            Column[] columns = new Column[readColumns.size()];

            for (int i = 0; i < readColumns.size(); i++) {
                columns[i] = new Column((String) readColumns.get(i), DataType.UNKNOWN);
            }

            String tableName = theDataFile.getName().substring(0, theDataFile.getName().indexOf(".csv"));
            ITableMetaData metaData = new DefaultTableMetaData(tableName, columns);
            _consumer.startTable(metaData);
            for (int i = 1; i < readData.size(); i++) {
                List rowList = (List) readData.get(i);
                Object[] row = rowList.toArray();
                for (int col = 0; col < row.length; col++) {
                    row[col] = row[col].equals(CsvDataSetWriter.NULL) ? null : row[col];
                }
                _consumer.row(row);
            }
            _consumer.endTable();
        } catch (PipelineException e) {
            throw new DataSetException(e);
        } catch (IllegalInputCharacterException e) {
            throw new DataSetException(e);
        } catch (IOException e) {
            throw new DataSetException(e);
        }
    }

}
