package com.qunar.base.qunit.casefilter;

import com.qunar.base.qunit.model.TestSuite;

/**
 * User: zhaohuiyu
 * Date: 2/17/13
 * Time: 12:03 PM
 */
public interface CaseFilter {
    void filter(TestSuite testSuite);
}
