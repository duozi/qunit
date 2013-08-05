/*
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */

package com.qunar.base.qunit.exception;

/**
 * TestCase的ID重复时一场信息
 *
 * Created by JarnTang at 12-5-31 上午10:48
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class DuplicateIdException extends RuntimeException{

    public DuplicateIdException(String message) {
        super(message);
    }

}
