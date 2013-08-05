package com.qunar.base.qunit.database.dataset;

import com.qunar.base.qunit.paramfilter.ParamFilter;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.ITableMetaData;

import java.util.Collections;
import java.util.List;

public class ParamFilterTable implements ITable {

    private ITable target;
    private List<ParamFilter> valueReplacerList;

    public ParamFilterTable(ITable target, List<ParamFilter> valueReplacer) {
        this.target = target;
        this.valueReplacerList = valueReplacer;
    }

    public ITableMetaData getTableMetaData() {
        return target.getTableMetaData();
    }

    public int getRowCount() {
        return target.getRowCount();
    }

    public Object getValue(int row, String column) throws DataSetException {
        Object value = target.getValue(row, column);
        if (value == null) return value;
        if (!(value instanceof String)) return value;
        for (ParamFilter valueReplacer : getValueReplacerList()) {
            value = valueReplacer.handle(value.toString());
        }
        return value;
    }

    public List<ParamFilter> getValueReplacerList() {
        if (valueReplacerList == null) {
            return Collections.emptyList();
        }
        return valueReplacerList;
    }
}