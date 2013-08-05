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

import java.util.Arrays;
import java.util.Map;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * the unit test for LoopStepCommand
 * <p/>
 * Created by JarnTang at 12-7-17 下午2:20
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class LoopStepCommandTest {

    private static final int time = 10;
    LoopStepCommand command;

    @Before
    public void setUp() throws Exception {
        command = new LoopStepCommand(time, null);
    }

    @Test
    public void should_invoke_config_times() throws Throwable {
        StepCommand command1 = mock(StepCommand.class);
        StepCommand command2 = mock(StepCommand.class);

        Response response = new HttpResponse(200, "");

        when(command1.doExecute(any(Response.class), any(Context.class))).thenReturn(response);
        when(command2.doExecute(any(Response.class), any(Context.class))).thenReturn(response);

        command = new LoopStepCommand(time, Arrays.asList(command1, command2));
        Response result = command.doExecute(null, null);

        assertThat(result, is(response));

        verify(command1, times(time)).doExecute(any(Response.class), any(Context.class));
        verify(command2, times(time)).doExecute(any(Response.class), any(Context.class));
    }

    @Test(expected = Throwable.class)
    public void should_return_exception_when_command_has_error() throws Throwable {
        StepCommand command1 = mock(StepCommand.class);

        when(command1.doExecute(any(Response.class), null)).thenThrow(new RuntimeException("execute failed."));

        command = new LoopStepCommand(time, Arrays.asList(command1));
        command.doExecute(null, null);
    }

    @Test
    public void testToReport() throws Exception {
        Map<String, Object> map = command.toReport();
        assertThat(map, hasKey("stepName"));
        assertThat(map, hasKey("params"));
    }

}
