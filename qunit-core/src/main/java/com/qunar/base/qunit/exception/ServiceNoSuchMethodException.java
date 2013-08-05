/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.exception;

/**
 * 对象方法不存在异常
 *
 * Created by JarnTang at 12-5-30 下午4:29
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class ServiceNoSuchMethodException extends RuntimeException{

    public ServiceNoSuchMethodException(String message) {
        super(message);
    }
}
