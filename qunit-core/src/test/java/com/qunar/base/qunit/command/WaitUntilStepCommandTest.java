/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.command;

import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.response.HttpResponse;
import com.qunar.base.qunit.response.Response;
import org.junit.Before;
import org.junit.Test;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.TimeoutException;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.Assert.assertThat;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * the unit test for WaitUntilStepCommand
 * <p/>
 * Created by JarnTang at 12-7-17 下午3:16
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class WaitUntilStepCommandTest {

    @Before
    public void setUp() throws Exception {
    }

    @Test
    public void should_wait_util_command_right_return() throws Throwable {
        final int count = 3;
        StepCommand command = mock(StepCommand.class);
        final Response response = new HttpResponse(200, "");

        WaitUntilStepCommand waitUntilStepCommand = new WaitUntilStepCommand(3000L, Arrays.asList(command));

        Answer<Object> errAnswer = new Answer<Object>() {
            int time;

            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                if (time < count - 1) {
                    time += 1;
                    throw new RuntimeException();
                } else {
                    return response;
                }
            }
        };
        given(command.doExecute(any(Response.class), any(Context.class))).willAnswer(errAnswer);
        waitUntilStepCommand.doExecute(null, null);
        verify(command, times(count)).doExecute(any(Response.class), any(Context.class));
    }

    @Test(expected = TimeoutException.class)
    public void should_throw_exception_unit_timeout() throws Throwable {
        StepCommand command = mock(StepCommand.class);

        WaitUntilStepCommand waitUntilStepCommand = new WaitUntilStepCommand(3000L, Arrays.asList(command));

        Answer<Object> errAnswer = new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocationOnMock) throws Throwable {
                throw new RuntimeException();
            }
        };

        given(command.doExecute(any(Response.class), any(Context.class))).willAnswer(errAnswer);
        waitUntilStepCommand.doExecute(null, null);
    }

    @Test
    public void testToReport() throws Exception {
        WaitUntilStepCommand command = new WaitUntilStepCommand(3000L, null);
        Map<String, Object> map = command.toReport();
        assertThat(map, hasKey("stepName"));
        assertThat(map, hasKey("params"));
    }

}
