/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.model;

import org.dbunit.operation.DatabaseOperation;

/**
 * 测试前，数据准备对数据库操作方式
 *
 * Created by JarnTang at 12-6-14 下午3:21
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public enum Operation {

    /**
     * 只进行插入操作，如果数据库中存在相同主键的数据，则报错退出
     */
    INSERT(DatabaseOperation.INSERT),

    /**
     * 更新操作，如果数据库存在相同主键的数据，更新其数据，否则报错退出
     */
    UPDATE(DatabaseOperation.UPDATE),

    /**
     * 删除数据库里与dataset文件中存在的数据
     */
    DELETE(DatabaseOperation.DELETE),

    /**
     * 删除dataset中存在的表的所有数据
     */
    DELETE_ALL(DatabaseOperation.DELETE_ALL),

    /**
     * 先根据准备数据中的id查询数据库，如果有对应的记录则更新，若无则插入新记录
     */
    REFRESH(DatabaseOperation.REFRESH),

    /**
     * 先删除所有相关的数据，然后再进行写入
     */
    CLEAR_INSERT(DatabaseOperation.CLEAN_INSERT),

    /**
     * 删除准备数据中相关的数据库表中的所有数据
     */
    TRUNCATE_TABLE(DatabaseOperation.TRUNCATE_TABLE);

    private DatabaseOperation databaseOperation;

    private Operation(DatabaseOperation databaseOperation) {
        this.databaseOperation = databaseOperation;
    }

    public DatabaseOperation valueOf(){
        return databaseOperation;
    }

}
