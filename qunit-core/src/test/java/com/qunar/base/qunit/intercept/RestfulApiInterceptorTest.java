package com.qunar.base.qunit.intercept;

import com.qunar.base.qunit.command.CallStepCommand;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;
import com.qunar.base.qunit.transport.command.ExecuteCommand;
import com.qunar.base.qunit.transport.command.HttpExecuteCommand;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static com.qunar.base.qunit.util.ReflectionUtils.getValue;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 7/16/12
 * Time: 10:43 AM
 */
public class RestfulApiInterceptorTest {

    private RestfulApiInterceptor interceptor;
    private ExecuteCommand httpCommand;
    private Context context;

    @Before
    public void setUp() throws Exception {
        interceptor = new RestfulApiInterceptor();
        httpCommand = new HttpExecuteCommand("test", "http://www.cnblogs.com/{bloger}.json", "get", "http");
        context = new Context(new Context(null));
    }

    @Test
    public void should_process_restful_api_and_removed_variable_in_url() {
        List<KeyValueStore> params = new ArrayList<KeyValueStore>();
        params.add(new KeyValueStore("bloger", "yuyijq"));
        params.add(new KeyValueStore("id", "1"));
        StepCommand callCommand = new CallStepCommand(httpCommand, params);

        interceptor.beforeExecute(callCommand, null, context);

        String url = (String) getValue(getValue(callCommand, "command"), "url");
        assertThat(url, is("http://www.cnblogs.com/yuyijq.json"));
        List<KeyValueStore> processedParams = (List<KeyValueStore>) getValue(callCommand, "params");

        assertThat(processedParams.size(), is(1));
        assertThat(processedParams.get(0).getName(), is("id"));
    }

    @Test
    public void should_process_restful_api_and_the_variable_come_from_pre_result() {
        List<KeyValueStore> params = new ArrayList<KeyValueStore>();
        params.add(new KeyValueStore("bloger", "$result.username"));
        params.add(new KeyValueStore("id", "1"));
        StepCommand callCommand = new CallStepCommand(httpCommand, params);
        Response preResult = new Response("{\"username\":\"yuyijq\"}", null);

        interceptor.beforeExecute(callCommand, preResult, context);

        String url = (String) getValue(getValue(callCommand, "command"), "url");
        assertThat(url, is("http://www.cnblogs.com/yuyijq.json"));
        List<KeyValueStore> processedParams = (List<KeyValueStore>) getValue(callCommand, "params");

        assertThat(processedParams.size(), is(1));
        assertThat(processedParams.get(0).getName(), is("id"));
    }
}
