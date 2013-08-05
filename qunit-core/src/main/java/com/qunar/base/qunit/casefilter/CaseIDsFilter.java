package com.qunar.base.qunit.casefilter;

import com.qunar.base.qunit.model.TestCase;
import com.qunar.base.qunit.model.TestSuite;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * User: zhaohuiyu
 * Date: 2/17/13
 * Time: 12:07 PM
 */
public class CaseIDsFilter implements CaseFilter {
    private List<String> ids;

    public CaseIDsFilter(String ids) {
        if (StringUtils.isBlank(ids)) {
            this.ids = Collections.EMPTY_LIST;
        } else {
            this.ids = Arrays.asList(StringUtils.split(ids, ","));
        }
    }

    @Override
    public void filter(TestSuite testSuite) {
        if (this.ids.isEmpty()) return;
        List<TestCase> needRerun = new ArrayList<TestCase>();
        for (TestCase testCase : testSuite.getTestCases()) {
            if (this.ids.contains(testCase.getId())) {
                needRerun.add(testCase);
            }
        }
        testSuite.setTestCases(needRerun);
    }
}
