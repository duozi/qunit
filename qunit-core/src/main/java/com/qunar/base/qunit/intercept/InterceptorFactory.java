/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.intercept;

import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.response.Response;

import java.util.ArrayList;
import java.util.List;

/**
 * 过滤器服务工厂类，负责管理所有过滤器
 * <p/>
 * Created by JarnTang at 12-7-9 下午4:43
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class InterceptorFactory {

    static List<Interceptor> interceptors = new ArrayList<Interceptor>();

    static {
        registerInterceptor(new HttpCallBackInterceptor());
        registerInterceptor(new RestfulApiInterceptor());
        registerInterceptor(new ParamIgnoreInterceptor());
        registerInterceptor(new UploadFileInterceptor());
    }

    public static void registerInterceptor(Interceptor interceptor) {
        interceptors.add(interceptor);
    }

    public void doBefore(StepCommand command, Response response, Context context) {
        for (Interceptor interceptor : interceptors) {
            interceptor.beforeExecute(command, response, context);
        }
    }

    public void doAfter(StepCommand command, Response response, Context context) {
        for (Interceptor interceptor : interceptors) {
            interceptor.afterExecute(command, response, context);
        }
    }

    public static InterceptorFactory getInstance() {
        return factory;
    }

    static InterceptorFactory factory = new InterceptorFactory();

}
