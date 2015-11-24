package com.qunar.base.qunit.command;

import com.qunar.base.qunit.config.CompareDatabaseStepConfig;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.database.DbUnitWrapper;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;
import com.qunar.base.qunit.util.CloneUtil;
import com.qunar.base.qunit.util.KeyValueUtil;
import org.apache.commons.lang.StringUtils;
import org.dbunit.Assertion;
import org.dbunit.dataset.*;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.filter.ExcludeTableFilter;

import java.util.*;

/**
 * User: zhaohuiyu
 * Date: 4/26/13
 * Time: 10:52 AM
 */
public class CompareDatabaseStepCommand extends ParameterizedCommand {

    private String database;
    private String expected;
    private String replaceTableName;
    private String ignore;
    private String orderBy;

    // tablename -> columns
    private Map<String, List<String>> ignoreColumns;
    private Map<String, List<String>> orderByColumns;

    public CompareDatabaseStepCommand(List<KeyValueStore> params) {
        super(params);
    }

    @Override
    protected Response doExecuteInternal(Response preResult,
                                         List<KeyValueStore> processedParams,
                                         Context context) throws Throwable {

        database = KeyValueUtil.getValueByKey(CompareDatabaseStepConfig.DATABASE, processedParams);
        expected = KeyValueUtil.getValueByKey(CompareDatabaseStepConfig.EXPECTED, processedParams);
        replaceTableName = KeyValueUtil.getValueByKey(CompareDatabaseStepConfig.REPLACETABLENAME, processedParams);
        ignore = KeyValueUtil.getValueByKey(CompareDatabaseStepConfig.IGNORE, processedParams);
        orderBy = KeyValueUtil.getValueByKey(CompareDatabaseStepConfig.ORDERBY, processedParams);
        computeIgnore();
        computeOrderBy();
        compare();
        return preResult;
    }


    private void computeIgnore() {
        ignoreColumns = computeColumns(ignore);
    }

    private void computeOrderBy() {
        orderByColumns = computeColumns(orderBy);
    }

    private Map<String, List<String>> computeColumns(String input) {
        Map<String, List<String>> result = new HashMap<String, List<String>>();
        if (StringUtils.isBlank(input)) return result;
        String[] tables = StringUtils.split(input, ";");
        for (String table : tables) {
            String temp = StringUtils.trim(table);
            if (StringUtils.isBlank(temp)) continue;
            if (temp.contains("(") && temp.endsWith(")")) {
                int index = temp.indexOf("(");
                String tableName = temp.substring(0, index);
                String columnStr = temp.substring(index + 1, temp.length() - 1);
                String[] columns = StringUtils.split(columnStr, ",");
                List<String> columnList = orderByColumns.get(tableName);
                if (columnList == null) {
                    columnList = new ArrayList<String>();
                    result.put(tableName, columnList);
                }
                columnList.addAll(Arrays.asList(columns));
            } else {
                if (!result.containsKey(temp)) {
                    result.put(temp, null);
                }
            }
        }
        return result;
    }

    private void compare() throws Throwable {
        DbUnitWrapper dbUnit = new DbUnitWrapper(database);
        IDataSet expectedDataSet = getExpectedDataSet(dbUnit);
        IDataSet actualDataSet = dbUnit.fetchDatabaseDataSet();
        compare(actualDataSet, expectedDataSet);
    }

    private void compare(IDataSet actualDataSet, IDataSet expectedDataSet) throws Throwable {
        String[] expectedTableNames = expectedDataSet.getTableNames();
        if (expectedTableNames.length == 0) return;
        String[] actualTableNames = actualDataSet.getTableNames();
        compareTableSize(actualTableNames, expectedTableNames);

        for (int i = 0; i < expectedTableNames.length; ++i) {
            String tableName = expectedTableNames[i];
            ITable expectedTable = expectedDataSet.getTable(tableName);
            //从期望的表里排除需要排除的列
            String[] ignoreColumns = getIgnoreColumns(tableName);
            if (ignoreColumns != null && ignoreColumns.length != 0) {
                expectedTable = DefaultColumnFilter.excludedColumnsTable(expectedTable, ignoreColumns);
            }

            ITable actualTable = actualDataSet.getTable(tableName);
            Column[] expectedColumns = expectedTable.getTableMetaData().getColumns();
            //只比较期望的表里存在的列
            actualTable = DefaultColumnFilter.includedColumnsTable(actualTable, expectedColumns);

            //对期望表和实际表排序后assert
            String[] orderByColumns = getOrderByColumns(tableName);
            if (orderByColumns != null && orderByColumns.length != 0) {
                ITable actualTableSorted = new SortedTable(actualTable, orderByColumns);
                ITable expectedTableSorted = new SortedTable(expectedTable, orderByColumns);
                Assertion.assertEquals(expectedTableSorted, actualTableSorted);
            } else {
                Assertion.assertEquals(expectedTable, actualTable);
            }

        }
    }

    /**
     * 排除指定的表
     *
     * @param dbunit
     * @return
     */
    private IDataSet getExpectedDataSet(DbUnitWrapper dbunit) {
        IDataSet dataSet = dbunit.generateDataSet(expected, replaceTableName, false);
        return new FilteredDataSet(new ExcludeTableFilter(getIgnoreTableNames()), dataSet);
    }

    private String[] getIgnoreTableNames() {
        List<String> result = new ArrayList<String>();
        for (Map.Entry<String, List<String>> entry : ignoreColumns.entrySet()) {
            if (entry.getValue() == null || entry.getValue().size() == 0) result.add(entry.getKey());
        }
        return result.toArray(new String[0]);
    }

    private String[] getIgnoreColumns(String tableName) {
        List<String> columns = ignoreColumns.get(tableName);
        if (columns == null || columns.size() == 0) return null;
        return columns.toArray(new String[0]);
    }

    private String[] getOrderByColumns(String tableName) {
        List<String> columns = orderByColumns.get(tableName);
        if (columns == null || columns.size() == 0) return null;
        return columns.toArray(new String[0]);
    }

    private void compareTableSize(String[] actualTableNames, String[] expectedTableNames) {
        if (expectedTableNames.length > actualTableNames.length) {
            String message = String.format("Expected include these tables: %s, but actual got: %s",
                    inANotInB(expectedTableNames, actualTableNames), StringUtils.join(actualTableNames, ","));
            throw new AssertionError(message);
        }
    }

    private String inANotInB(String[] a, String[] b) {
        List<String> result = new ArrayList<String>();
        for (String aItem : a) {
            for (String bItem : b) {
                if (aItem.equals(bItem)) continue;
            }
            result.add(aItem);
        }
        return StringUtils.join(result, ",");
    }

    @Override
    protected StepCommand doClone() {
        return new CompareDatabaseStepCommand(CloneUtil.cloneKeyValueStore(params));
    }

    @Override
    public Map<String, Object> toReport() {
        Map<String, Object> details = new HashMap<String, Object>();
        details.put("stepName", "数据库比对:");
        details.put("name", String.format("对数据库%s进行比对", database));

        ArrayList<KeyValueStore> params = new ArrayList<KeyValueStore>();
        params.add(new KeyValueStore("database", database));
        params.add(new KeyValueStore("expected", expected));
        params.add(new KeyValueStore("忽略的表和列", ignoreColumns));
        details.put("params", params);

        return details;
    }
}
