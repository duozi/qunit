/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.database.dataset;

import com.qunar.base.qunit.paramfilter.ParamFilter;
import org.dbunit.dataset.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

/**
 * 值替换DataSet
 * <p/>
 * Created by JarnTang at 12-7-23 下午5:02
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class ParamFilterDataSet extends AbstractDataSet {
    private final Logger logger = LoggerFactory.getLogger(ParamFilterDataSet.class);

    private IDataSet dataSet;
    private List<ParamFilter> valueReplacerList;

    public ParamFilterDataSet(IDataSet iDataSet, List<ParamFilter> valueReplacer) {
        dataSet = iDataSet;
        this.valueReplacerList = valueReplacer;
    }

    @Override
    protected ITableIterator createIterator(boolean reversed) throws DataSetException {
        return new ReplacementIterator(dataSet.iterator());
    }

    private ParamFilterTable createReplacementTable(ITable table) {
        logger.debug("createReplacementTable(table={}) - start", table);
        return new ParamFilterTable(table, getValueReplacerList());
    }

    public synchronized List<ParamFilter> getValueReplacerList() {
        if (valueReplacerList == null) {
            valueReplacerList = new ArrayList<ParamFilter>();
        }
        return valueReplacerList;
    }

    private class ReplacementIterator implements ITableIterator {
        private final Logger logger = LoggerFactory.getLogger(ReplacementIterator.class);
        private final ITableIterator _iterator;

        public ReplacementIterator(ITableIterator iterator) {
            _iterator = iterator;
        }

        public boolean next() throws DataSetException {
            logger.debug("next() - start");
            return _iterator.next();
        }

        public ITableMetaData getTableMetaData() throws DataSetException {
            logger.debug("getTableMetaData() - start");
            return _iterator.getTableMetaData();
        }

        public ITable getTable() throws DataSetException {
            logger.debug("getTable() - start");
            return createReplacementTable(_iterator.getTable());
        }
    }

}
