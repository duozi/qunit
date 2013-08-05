/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.statement;

import com.qunar.base.qunit.QunitFrameworkMethod;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.event.StepNotifier;
import com.qunar.base.qunit.model.TestCase;
import com.qunar.base.qunit.reporter.QJSONReporter;
import com.qunar.base.qunit.response.Response;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

/**
 * 描述：
 * Created by JarnTang at 12-8-1 上午10:41
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class QunitStatementTest {

    private Context caseContext;

    @Before
    public void setUp() throws Exception {
        caseContext = new Context(new Context(null));
    }

    @Test(expected = RuntimeException.class)
    public void testEvaluate() throws Throwable {

        QunitFrameworkMethod qunitFrameworkMethod = mock(QunitFrameworkMethod.class);
        StepCommand bodyCommand = mock(StepCommand.class);
        StepCommand tearDownCommand = mock(StepCommand.class);
        QunitStatement statement = new QunitStatement(qunitFrameworkMethod, new QJSONReporter(System.out));

        TestCase testCase = new TestCase();
        testCase.setBodyCommand(bodyCommand);
        testCase.setTearDownCommand(tearDownCommand);

        when(qunitFrameworkMethod.getTestCase()).thenReturn(testCase);
        when(qunitFrameworkMethod.getContext()).thenReturn(caseContext);

        doThrow(RuntimeException.class).when(bodyCommand).execute(any(Response.class), any(Context.class), any(StepNotifier.class));
        doNothing().when(tearDownCommand).execute(any(Response.class), any(Context.class), any(StepNotifier.class));

        try {
            statement.evaluate();
        } finally {
            verify(bodyCommand, times(1)).execute(any(Response.class), any(Context.class), any(StepNotifier.class));
            verify(tearDownCommand, times(1)).execute(any(Response.class), any(Context.class), any(StepNotifier.class));
        }

    }

}
