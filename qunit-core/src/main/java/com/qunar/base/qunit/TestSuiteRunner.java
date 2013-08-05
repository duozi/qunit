/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit;

import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.DataDrivenTestCase;
import com.qunar.base.qunit.model.TestCase;
import com.qunar.base.qunit.model.TestSuite;
import com.qunar.base.qunit.reporter.Reporter;
import com.qunar.base.qunit.statement.QunitStatement;
import org.apache.commons.lang.StringUtils;
import org.junit.internal.AssumptionViolatedException;
import org.junit.internal.runners.model.EachTestNotifier;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.Statement;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static com.qunar.base.qunit.util.CommandUtil.concatCommand;

/**
 * 描述：
 * Created by JarnTang at 12-5-18 下午4:29
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class TestSuiteRunner extends BlockJUnit4ClassRunner {

    private TestSuite testSuite;
    private Context suitContext;

    private Reporter reporter = null;

    static {
        ClassLoader.getSystemClassLoader().setDefaultAssertionStatus(true);
    }

    public TestSuiteRunner(Class clazz, TestSuite testSuite, Context suitContext, Reporter rpt) throws org.junit.runners.model.InitializationError {
        super(clazz);
        this.testSuite = testSuite;
        this.reporter = rpt;
        this.suitContext = suitContext;
    }

    @Override
    public void run(RunNotifier notifier) {
        try {
            this.reporter.report(testSuite);
            super.run(notifier);
        } finally {
            this.reporter.done();
        }
    }

    @Override
    protected List<FrameworkMethod> computeTestMethods() {
        if (isTestWithFile()) {
            List<FrameworkMethod> frameworkMethodList = new ArrayList<FrameworkMethod>();
            addTestCase(frameworkMethodList, testSuite.getBackGrounds());
            addTestCase(frameworkMethodList, testSuite.getBeforeSuite());

            withBeforeAndAfterCommand(frameworkMethodList);

            addTestCase(frameworkMethodList, testSuite.getAfterSuite());
            return frameworkMethodList;
        } else {
            return super.computeTestMethods();
        }
    }

    private StepCommand buildStepCommand(List<TestCase> testCases) {
        if (testCases == null || testCases.size() < 1) {
            return null;
        }
        StepCommand command = null;
        for (TestCase tc : testCases) {
            command = concatCommand(command, tc.getBodyCommand().cloneCommand());
        }
        return command;
    }

    private void withBeforeAndAfterCommand(List<FrameworkMethod> frameworkMethodList) {
        List<TestCase> testCases = testSuite.getTestCases();
        if (testCases == null || testCases.size() < 1) {
            return;
        }
        for (TestCase testCase : testCases) {
            testCase.setBeforeCommand(buildStepCommand(testSuite.getBeforeCase()));
            testCase.setAfterCommand(buildStepCommand(testSuite.getAfterCase()));
            if (testCase instanceof DataDrivenTestCase) {
                addDataDrivenTestCases(frameworkMethodList, (DataDrivenTestCase) testCase, suitContext);
            } else {
                Context caseContext = new Context(suitContext);
                frameworkMethodList.add(new QunitFrameworkMethod(null, testCase, caseContext));
            }
        }
    }

    private void addDataDrivenTestCases(List<FrameworkMethod> frameworkMethodList, DataDrivenTestCase testCase, Context suiteContext) {
        List<Map<String, String>> examples = testCase.getExamples();
        int index = 0;
        for (Map<String, String> data : examples) {
            Context caseContext = new Context(suiteContext);
            for (Map.Entry<String, String> entry : data.entrySet()) {
                caseContext.addContext(entry.getKey(), entry.getValue());
            }
            TestCase newTestCase = testCase.clone();
            newTestCase.setDesc(newTestCase.getDesc() + index++);
            frameworkMethodList.add(new QunitFrameworkMethod(null, newTestCase, caseContext));
        }
    }

    private void addTestCase(List<FrameworkMethod> frameworkMethodList, List<TestCase> testCases) {
        if (testCases == null || testCases.size() < 1) {
            return;
        }
        for (TestCase testCase : testCases) {
            frameworkMethodList.add(new QunitFrameworkMethod(null, testCase, suitContext));
        }
    }

    @Override
    protected Statement methodInvoker(FrameworkMethod method, Object test) {
        if (method instanceof QunitFrameworkMethod) {
            return new QunitStatement((QunitFrameworkMethod) method, this.reporter);
        }
        return super.methodInvoker(method, test);
    }

    private boolean isTestWithFile() {
        return getTestClass().getJavaClass().getAnnotation(Qunit.Options.class) != null || testSuite != null;
    }

    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        EachTestNotifier eachNotifier = makeNotifier(method, notifier);
        runNotIgnored(method, eachNotifier);
    }

    private EachTestNotifier makeNotifier(FrameworkMethod method, RunNotifier notifier) {
        Description description = describeChild(method);
        return new EachTestNotifier(notifier, description);
    }

    private void runNotIgnored(FrameworkMethod method, EachTestNotifier eachNotifier) {
        eachNotifier.fireTestStarted();
        try {
            Statement statement = methodBlock(method);
            statement.evaluate();
        } catch (AssumptionViolatedException e) {
            eachNotifier.addFailedAssumption(e);
        } catch (Throwable e) {
            eachNotifier.addFailure(e);
        } finally {
            eachNotifier.fireTestFinished();
        }
    }

    /*
    HAKE:
    In supper class BlockJUnit4ClassRunner's ctor will call this method(validateInstanceMethods),
    validateInstanceMethods will call:
    if (computeTestMethods().size() == 0)
			errors.add(new Exception("No runnable methods"));

	but at this time testSuite in computeTestMethods is null, this will cause NullPointerException.
     */
    @Override
    protected void validateInstanceMethods(List<Throwable> errors) {
    }

    @Override
    protected String getName() {
        String desc = testSuite.getDesc();
        if (StringUtils.isBlank(desc)) {
            desc = testSuite.getId();
        }
        return desc + "-" + getShortFileName(testSuite.getCaseFileName());
    }

    private String getShortFileName(String fileName) {
        int i = fileName.lastIndexOf(File.separator);
        if (i < 0) {
            return fileName;
        }
        return fileName.substring(i + 1);
    }

}
