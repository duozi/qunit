package com.qunar.base.qunit.event;

import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.TestCase;

/**
 * User: zhaohuiyu
 * Date: 7/4/12
 * Time: 4:03 PM
 */
public interface StepEventListener {
    void caseStarted(TestCase testCase, Context context);

    void caseFinished(TestCase testCase, Context context);

    void stepStarted(StepCommand sc);

    void stepFailed(StepCommand sc, Throwable e);

    void stepFinished(StepCommand sc);
}
