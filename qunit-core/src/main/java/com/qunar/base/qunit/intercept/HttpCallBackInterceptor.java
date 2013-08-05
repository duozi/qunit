/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.intercept;

import com.qunar.base.qunit.command.CallStepCommand;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.response.HttpResponse;
import com.qunar.base.qunit.response.Response;
import org.apache.commons.lang.CharSetUtils;
import org.apache.commons.lang.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 解析http接口带有callback函数的请求，把callback函数名解析出来
 * <p/>
 * Created by JarnTang at 12-6-29 下午4:18
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class HttpCallBackInterceptor implements StepCommandInterceptor {
    final static Pattern pattern = Pattern.compile("^[\\s]*([a-zA-Z_]{1}[\\w\\.]*)?\\('?(.*)'?\\)[\\s]*;?[\\s]*$");

    @Override
    public Object beforeExecute(StepCommand command, Response param, Context context) {
        return param;
    }

    @Override
    public Object afterExecute(StepCommand command, Response response, Context context) {
        if (!(command instanceof CallStepCommand)) {
            return response;
        }

        if (response != null && response instanceof HttpResponse) {
            if (response.getBody() != null && response.getBody() instanceof String) {
                String body = StringUtils.trim((String) response.getBody());
                body = CharSetUtils.delete(body, "\r\t\n");
                String callbackFunctionName = getCallbackFunctionName(body);
                String realBody = getRealBodyWithoutCallbackName(body);
                if (noCallback(body, realBody)) {
                    return response;
                }
                ((HttpResponse) response).setCallback(callbackFunctionName);
                response.setBody(realBody);
            }
        }
        return response;
    }

    private boolean noCallback(String body, String realBody) {
        return body.equals(realBody);
    }

    private String getRealBodyWithoutCallbackName(String body) {
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()) {
            return matcher.group(2);
        }
        return body;
    }

    private String getCallbackFunctionName(String body) {
        Matcher matcher = pattern.matcher(body);
        if (matcher.find()) {
            String name = matcher.group(1);
            if (!"".equals(name)) {
                return name;
            }
        }
        return null;
    }

}
