/**
 *
 */
package com.qunar.base.qunit.reporter;

import com.qunar.base.qunit.dsl.DSLCommandDesc;
import com.qunar.base.qunit.model.ServiceDesc;
import com.qunar.base.qunit.model.SvnInfo;
import com.qunar.base.qunit.model.TestSuite;
import com.qunar.base.qunit.reporter.QJSONReporter.ReporterEventListener;

/**
 * Interface for reporting results.
 *
 * @author ziqiang.deng
 */
public interface Reporter {

    /**
     * called in a runner before running an XML file
     */
    void report(TestSuite testSuite);

    /**
     * called in a runner after running an XML file
     */
    void done();

    /**
     * return case reports as a String instance
     */
    String reportAsString();

    /**
     * */
    void close();

    /**
     * */
    ReporterEventListener createStepListener();

    void addSvnInfo(SvnInfo svnInfo);

    void addService(ServiceDesc serviceDesc);

    void addDSLCommand(DSLCommandDesc dslCommandDesc);
}
