/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit;

import com.qunar.base.qunit.annotation.Filter;
import com.qunar.base.qunit.annotation.Interceptor;
import com.qunar.base.qunit.casefilter.CaseFilter;
import com.qunar.base.qunit.casereader.DatacaseReader;
import com.qunar.base.qunit.casereader.Dom4jCaseReader;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.dsl.DSLCommandReader;
import com.qunar.base.qunit.intercept.InterceptorFactory;
import com.qunar.base.qunit.intercept.StepCommandInterceptor;
import com.qunar.base.qunit.model.*;
import com.qunar.base.qunit.paramfilter.FilterFactory;
import com.qunar.base.qunit.paramfilter.ParamFilter;
import com.qunar.base.qunit.reporter.Reporter;
import com.qunar.base.qunit.transport.command.ServiceFactory;
import com.qunar.base.qunit.util.IpUtil;
import com.qunar.base.qunit.util.PropertyUtils;
import com.qunar.base.qunit.util.ReflectionUtils;
import com.qunar.base.validator.JsonValidator;
import com.qunar.base.validator.factories.ValidatorFactory;
import com.qunar.base.validator.validators.Validator;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.dom4j.DocumentException;
import org.junit.runner.Description;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.ParentRunner;
import org.junit.runners.model.InitializationError;

import java.io.FileNotFoundException;
import java.lang.annotation.*;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Qunit测试的入口类，使用时通过RunWith注解指定Junit的runner
 * <p/>
 * Created by JarnTang at 12-5-19 下午3:34
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class Qunit extends ParentRunner<TestSuiteRunner> {

    private final static Context GLOBALCONTEXT = new Context(null);

    private List<TestSuiteRunner> children = new ArrayList<TestSuiteRunner>();

    private Reporter qjsonReporter;

    private final CaseFilter filter;

    private QunitOptions options;

    public Qunit(Class<?> testClass) throws InitializationError, DocumentException, FileNotFoundException {
        super(testClass);
        options = new QunitOptions(testClass);

        addJobAndIdToContext(options);
        MonitorLog.start(options);
        setArrayValidateMethod();

        List<String> files = options.testCases();
        List<String> dataFiles = options.dataCases();
        ensureHasCases(files, dataFiles);
        List<String> beforeFiles = options.before();
        List<String> afterFiles = options.after();

        this.qjsonReporter = options.reporter();

        SvnInfo svnInfo = new SvnInfoReader().read();
        this.qjsonReporter.addSvnInfo(svnInfo);

        determinedLocalHost();

        attatchHandlers(testClass);
        attatchInterceptors(testClass);

        List<DataSuite> suites = null;
        if (CollectionUtils.isNotEmpty(dataFiles)){
            List<String> levels = options.levels();
            List<String> statuss = options.statuss();

            DatacaseReader datacaseReader = new DatacaseReader();
            suites = datacaseReader.getSuites(dataFiles, options.keyFile(), options.dslFile());
            datacaseReader.processDataSuite(suites, levels, statuss);
        }

        new DSLCommandReader().read(options.dslFile(), qjsonReporter);

        ServiceFactory.getInstance().init(options.serviceConfig(), qjsonReporter);
        Environment.initEnvironment(testClass);

        filter = options.createCaseFilter();

        addChildren(beforeFiles, null);
        addChildren(files, suites);
        addChildren(afterFiles, null);
    }

    private void addJobAndIdToContext(QunitOptions options) {
        if (StringUtils.isNotBlank(options.jobName()) && StringUtils.isNotBlank(options.jobName())) {
            GLOBALCONTEXT.addContext("job", options.jobName());
            GLOBALCONTEXT.addContext("build", options.buildNumber());
        }
    }

    private void setArrayValidateMethod() {
        String property = PropertyUtils.getProperty("array_default_order_validate", "false");
        JsonValidator.arrayDefaultOrderValidate = Boolean.valueOf(property);
    }

    private void ensureHasCases(List<String> files, List<String> dataFiles) {
        if ((files == null || files.size() == 0) && (dataFiles == null || dataFiles.size() == 0)) {
            throw new RuntimeException("Case文件不存在: 请检查你指定的case文件是否存在");
        }
    }

    private void determinedLocalHost() {
        GLOBALCONTEXT.addContext("jenkins.host", IpUtil.getLocalNetworkAddress());
    }

    private void attatchInterceptors(Class<?> testClass) {
        if (!testClass.isAnnotationPresent(Interceptor.class)) return;
        Interceptor interceptor = testClass.getAnnotation(Interceptor.class);
        Class<? extends StepCommandInterceptor>[] value = interceptor.value();
        for (Class<? extends StepCommandInterceptor> interceptorClass : value) {
            StepCommandInterceptor stepCommandInterceptor = ReflectionUtils.newInstance(interceptorClass);
            InterceptorFactory.registerInterceptor(stepCommandInterceptor);
        }
    }

    private void attatchHandlers(Class<?> testClass) {
        Field[] fields = testClass.getDeclaredFields();
        for (Field field : fields) {
            if (isFilter(field)) {
                ParamFilter filter = (ParamFilter) ReflectionUtils.newInstance((Class<? extends ParamFilter>) field.getType());
                FilterFactory.register(filter);
            }
        }
    }

    private boolean isFilter(Field field) {
        return field.isAnnotationPresent(Filter.class)
                && ParamFilter.class.isAssignableFrom(field.getType());
    }

    @Override
    protected List<TestSuiteRunner> getChildren() {
        return children;
    }

    @Override
    protected Description describeChild(TestSuiteRunner child) {
        return child.getDescription();
    }

    @Override
    protected void runChild(TestSuiteRunner child, RunNotifier notifier) {
        child.run(notifier);
    }

    @Override
    public void run(RunNotifier notifier) {
        try {
            super.run(notifier);
        } finally {
            qjsonReporter.close();
        }
    }

    private void addChildren(List<String> files, List<DataSuite> dataSuites) throws InitializationError, DocumentException, FileNotFoundException {
        List<TestSuite> suites = new ArrayList<TestSuite>(files.size());
        for (String file : files) {
            TestSuite testSuite = new Dom4jCaseReader().readTestCase(file);
            if (testSuite == null) continue;
            filter.filter(testSuite);
            if (!testSuite.getTestCases().isEmpty()) {
                suites.add(testSuite);
            }
        }
        if (dataSuites != null){
            suites.addAll(new DatacaseReader().convertDataSuiteToTestSuite(dataSuites, this.options.ids()));
        }
        Collections.sort(suites);
        for (TestSuite suite : suites) {
            Context suitContext = new Context(GLOBALCONTEXT);
            children.add(new TestSuiteRunner(getTestClass().getJavaClass(), suite, suitContext, this.qjsonReporter));
        }
    }

    public static void registerValidator(String validatorName, Class<? extends Validator> validatorClass) {
        ValidatorFactory.registerValidator(validatorName, validatorClass);
    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.TYPE)
    @Documented
    @Inherited
    public @interface Options {

        String[] files() default "";

        String[] before() default "";

        String[] after() default "";

        String[] tags() default "*";

        String[] levels() default "*";

        String[] statuss() default "*";

        String ids() default "";

        String[] service() default "service.xml";

        String[] dsl() default "";

        String[] dataFiles() default "";

        String keyFile() default "cases/key.xml";

        Operation operation() default Operation.CLEAR_INSERT;
    }

}
