/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.intercept;

import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.response.Response;

/**
 * 描述：
 * Created by JarnTang at 12-7-9 下午12:10
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public interface StepCommandInterceptor extends Interceptor<StepCommand, Response, Response> {

}
