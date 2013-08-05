package com.qunar.base.qunit.command;

/**
 * User: xiaofen.zhang
 * Date: 12-6-7
 * Time: 下午2:31
 */
public abstract class Transform<T> {

    public abstract Object transport(T transformBody) throws Throwable;

}
