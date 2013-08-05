/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.transport.exception;

/**
 * 远程服务初始化异常
 *
 * Created by JarnTang at 12-5-30 下午4:24
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class InitRpcServiceException extends RuntimeException{

    public InitRpcServiceException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
