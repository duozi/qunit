/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.statement;

import com.qunar.base.qunit.QunitFrameworkMethod;
import com.qunar.base.qunit.Statistics;
import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.event.StepNotifier;
import com.qunar.base.qunit.model.TestCase;
import com.qunar.base.qunit.reporter.ParseCase;
import com.qunar.base.qunit.reporter.QJSONReporter;
import com.qunar.base.qunit.reporter.Reporter;
import org.junit.runners.model.Statement;

import java.util.List;
import java.util.Map;

/**
 * 描述：
 * Created by JarnTang at 12-5-19 下午2:43
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class QunitStatement extends Statement {

    private QunitFrameworkMethod frameworkMethod;

    private Reporter reporter;

    public QunitStatement(QunitFrameworkMethod frameworkMethod, Reporter rpt) {
        this.frameworkMethod = frameworkMethod;
        this.reporter = rpt;
    }

    @Override
    public void evaluate() throws Throwable {
        StepNotifier sNotifier = new StepNotifier();
        sNotifier.addStepEventListener(this.reporter.createStepListener());
        TestCase testCase = frameworkMethod.getTestCase();
        try {
            sNotifier.fireCaseStarted(testCase, frameworkMethod.getContext());
            runBeforeCommand(sNotifier, testCase);
            runPipeline(sNotifier, testCase);
        } finally {
            runTearDownCommand(sNotifier, testCase);
            runAfterCommand(sNotifier, testCase);
            sNotifier.fireCaseFinished(testCase, frameworkMethod.getContext());

            QJSONReporter qjsonReporter = (QJSONReporter)reporter;
            Map<Object, Object> suitMap = qjsonReporter.getSuiteMap();
            List<Object> elements = (List<Object>)suitMap.get("elements");
            Map<Object, Object> lastElement = (Map<Object, Object>)elements.get(elements.size() - 1);
            ParseCase parseCase = new ParseCase(qjsonReporter, lastElement);
            new Thread(parseCase).start();
            Statistics.start(qjsonReporter.getCaseStatistics());
        }
    }

    private void runAfterCommand(StepNotifier sNotifier, TestCase testCase) throws Throwable {
        StepCommand afterCommand = testCase.getAfterCommand();
        if (afterCommand != null) {
            afterCommand.execute(null, frameworkMethod.getContext(), sNotifier);
        }
    }

    private void runTearDownCommand(StepNotifier sNotifier, TestCase testCase) throws Throwable {
        StepCommand tearDownCommand = testCase.getTearDownCommand();
        if (tearDownCommand != null) {
            tearDownCommand.execute(null, frameworkMethod.getContext(), sNotifier);
        }
    }

    private void runPipeline(StepNotifier sNotifier, TestCase testCase) throws Throwable {
        StepCommand pipeline = testCase.pipeline();
        if (pipeline != null) {
            pipeline.execute(null, frameworkMethod.getContext(), sNotifier);
        }
    }

    private void runBeforeCommand(StepNotifier sNotifier, TestCase testCase) throws Throwable {
        StepCommand beforeCommand = testCase.getBeforeCommand();
        if (beforeCommand != null) {
            beforeCommand.execute(null, frameworkMethod.getContext(), sNotifier);
        }
    }

}
