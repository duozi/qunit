/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.intercept;

import com.qunar.base.qunit.command.CallStepCommand;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.response.HttpResponse;
import org.apache.commons.lang.CharSetUtils;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.nullValue;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;

/**
 * HttpCallBackInterceptor unit test
 * <p/>
 * Created by JarnTang at 12-7-10 下午12:09
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class HttpCallBackInterceptorTest {

    private static HttpCallBackInterceptor callBackInterceptor;
    private CallStepCommand command;
    private Context caseContext;

    @BeforeClass
    public static void beforeClass() {
        callBackInterceptor = new HttpCallBackInterceptor();
    }

    @Before
    public void setUp() throws Exception {
        command = new CallStepCommand(null, null);
        caseContext = new Context(new Context(null));
    }

    @Test
    public void should_return_right_callback_name() throws Exception {
        String callbackName = "callbackName";
        String body = "{alert('hello world.')}";
        String callbackBody = callbackName + "(" + body + ")";
        HttpResponse response = new HttpResponse(200, callbackBody);
        callBackInterceptor.afterExecute(command, response, caseContext);
        assertThat(response.getCallback(), is(callbackName));
        assertThat((String) response.getBody(), is(body));
    }

    @Test
    public void should_not_break_normal_reponse_given_not_match_callback_pattern() throws Exception {
        String json = "{alert('hello world.')}";
        HttpResponse response = new HttpResponse(200, json);
        callBackInterceptor.afterExecute(command, response, caseContext);
        assertNull(response.getCallback());
        assertThat((String) response.getBody(), is(json));
    }

    @Test
    public void should_return_right_callback_name_with_enter_and_lineFeed() {
        String functionName = "XQScript_9";
        String body = "{fee:348,childFee:0,isQFee:0,alreadyContain:false}";
        String json = "\r\n\r\n\r\n" + functionName + "(" + body + ")" + "\n\r\n";

        HttpResponse response = new HttpResponse(200, json);
        callBackInterceptor.afterExecute(command, response, caseContext);
        assertThat(response.getCallback(), is("XQScript_9"));
        assertThat((String) response.getBody(), is(body));
    }

    @Test
    public void should_return_right_callback_name_endWith_commaSymbol() {
        String functionName = "XQScript_9";
        String body = "{fee:348,childFee:0,isQFee:0,alreadyContain:false}";
        String json = "\r\n\r\n\r\n" + functionName + "(" + body + ");" + "\n\r\n";

        HttpResponse response = new HttpResponse(200, json);
        callBackInterceptor.afterExecute(command, response, caseContext);
        assertThat(response.getCallback(), is("XQScript_9"));
        assertThat((String) response.getBody(), is(body));
    }

    @Test
    public void should_return_right_callback_name_contains_dot() {
        String functionName = "XQScript_9.method";
        String body = "{fee:348,childFee:0,isQFee:0,alreadyContain:false}";
        String json = "\r\n\r\n\r\n" + functionName + "(" + body + ");" + "\n\r\n";

        HttpResponse response = new HttpResponse(200, json);
        callBackInterceptor.afterExecute(command, response, caseContext);
        assertThat(response.getCallback(), is(functionName));
        assertThat((String) response.getBody(), is(body));
    }

    @Test
    public void should_right_when_no_callback_function_name() {
        String body = "{\"ret\":false,\"errcode\":3,\"errmsg\":\"order not exist\"}";
        String json = "(" + body + ")";
        HttpResponse response = new HttpResponse(200, json);
        callBackInterceptor.afterExecute(command, response, caseContext);
        assertThat(response.getCallback(), nullValue());
        assertThat((String) response.getBody(), is(body));
    }

    @Test
    public void should_return_right_body_when_no_callback_function_name_and_with_lineFeed_prefix() {
        String body = "{\"ret\":false,\"errcode\":3,\"errmsg\":\"order not exist\"}";
        String json = "\n\r\t(" + body + ")";
        HttpResponse response = new HttpResponse(200, json);
        callBackInterceptor.afterExecute(command, response, caseContext);
        assertThat(response.getCallback(), nullValue());
        assertThat((String) response.getBody(), is(body));
    }

    @Test
    public void should_return_body_when_body_include_break_sybomls() {
        String body = "{\"ret\":false,\"errcode\":3,\n" +
                "\"errmsg\":\"order not exist\"}";
        String json = "\n\r\t(" + body + ")";
        HttpResponse response = new HttpResponse(200, json);
        callBackInterceptor.afterExecute(command, response, caseContext);
        assertThat(response.getCallback(), nullValue());
        assertThat((String) response.getBody(), is(CharSetUtils.delete(body, "\n")));
    }

}
