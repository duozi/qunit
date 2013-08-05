/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.intercept;

import com.qunar.base.qunit.context.Context;

/**
 * 拦截器
 * <p/>
 * Created by JarnTang at 12-6-29 下午4:13
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public interface Interceptor<Command, T, K> {

    public Object beforeExecute(Command command, T param, Context context);

    public Object afterExecute(Command command, K response, Context context);

}
