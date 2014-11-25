package com.qunar.base.qunit.database.dataset;

import com.qunar.base.qunit.database.dataset.csv.QCsvDataSet;
import com.qunar.base.qunit.paramfilter.Clock;
import com.qunar.base.qunit.paramfilter.DateParamFilter;
import com.qunar.base.qunit.util.ReaderUtil;
import org.apache.commons.lang.StringUtils;
import org.dbunit.dataset.*;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * User: zhaohuiyu
 * Date: 12/17/12
 */
public class CachedDataSet implements IDataSet {
    private final static Logger logger = LoggerFactory.getLogger(CachedDataSet.class);

    private static Map<String, IDataSet> CACHED_DATASET = new HashMap<String, IDataSet>();

    private IDataSet innerDataSet;

    public CachedDataSet(String file, String replaceStr, boolean cached) {
        this.innerDataSet = CACHED_DATASET.get(file);
        if (this.innerDataSet == null) {
            this.innerDataSet = build(file, replaceStr, cached);
            CACHED_DATASET.put(file, this.innerDataSet);
        }
    }

    @Override
    public String[] getTableNames() throws DataSetException {
        return innerDataSet.getTableNames();
    }

    @Override
    public ITableMetaData getTableMetaData(String tableName) throws DataSetException {
        return innerDataSet.getTableMetaData(tableName);
    }

    @Override
    public ITable getTable(String tableName) throws DataSetException {
        return innerDataSet.getTable(tableName);
    }

    @Override
    public ITable[] getTables() throws DataSetException {
        return innerDataSet.getTables();
    }

    @Override
    public ITableIterator iterator() throws DataSetException {
        return innerDataSet.iterator();
    }

    @Override
    public ITableIterator reverseIterator() throws DataSetException {
        return innerDataSet.reverseIterator();
    }

    @Override
    public boolean isCaseSensitiveTableNames() {
        return innerDataSet.isCaseSensitiveTableNames();
    }

    private IDataSet build(String file, String replaceStr, boolean cached) {
        try {
            if (file.endsWith(".xml")) {
                FlatXmlDataSetBuilder flatXmlDataSetBuilder = new FlatXmlDataSetBuilder();
                flatXmlDataSetBuilder.setColumnSensing(cached);
                return flatXmlDataSetBuilder
                        .build(readFileAndReplaceTableName(file, replaceStr));
            } else if (file.endsWith(".csv")) {
                URL resource = this.getClass().getClassLoader().getResource(file);
                if (resource == null) {
                    throw new RuntimeException(String.format("file[%s] not found", file));
                }
                return new QCsvDataSet(new File(resource.getPath()));
            } else {
                throw new IllegalStateException("DbUnit only supports CSV or Flat XML data sets for the moment");
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private InputStream readFileAndReplaceTableName(String file, String replaceStr) {
        InputStream inputStream = this.getClass().getClassLoader().getResourceAsStream(file);
        if (inputStream == null) {
            throw new RuntimeException(String.format("dataSet file [%s] is not found.", file));
        }
        if (StringUtils.isBlank(replaceStr)) return inputStream;

        String content = ReaderUtil.readeAsString(inputStream);
        String[] split = replaceStr.split("->");
        if (split.length != 2) {
            logger.warn("prepare command replace table name failed, replace config is " + replaceStr);
            return inputStream;
        }
        content = content.replaceAll("<" + split[0], "<" + new DateParamFilter(new Clock()).handle(split[1]));
        return new ByteArrayInputStream(content.getBytes());
    }
}
