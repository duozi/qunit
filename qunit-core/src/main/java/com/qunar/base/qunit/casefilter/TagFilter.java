package com.qunar.base.qunit.casefilter;

import com.qunar.base.qunit.model.TestCase;
import com.qunar.base.qunit.model.TestSuite;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;

/**
 * User: zhaohuiyu
 * Date: 11/2/12
 */
public class TagFilter implements CaseFilter {
    private List<String> expectedTags;

    public TagFilter(List<String> expectedTags) {
        this.expectedTags = expectedTags;
    }

    public void filter(TestSuite testSuite) {
        if (matchAllTags()) return;
        if (ignoreTestSuitTags(testSuite)) {
            List<TestCase> testCases = testSuite.getTestCases();
            testSuite.setTestCases(filterCaseByTags(testCases, false));
            return;
        }

        if (!tagCanBeMatch(testSuite.getTags(), true)) {
            testSuite.setTestCases(new ArrayList<TestCase>(0));
        } else {
            List<TestCase> testCases = testSuite.getTestCases();
            List<TestCase> runableCases = new ArrayList<TestCase>();
            runableCases.addAll(filterCaseByTags(testCases, true));
            runableCases.addAll(withoutTags(testCases));
            testSuite.setTestCases(new ArrayList<TestCase>(new LinkedHashSet<TestCase>(runableCases)));
        }

    }

    private boolean matchAllTags() {
        return expectedTags.contains("*");
    }

    private List<TestCase> withoutTags(List<TestCase> testCases) {
        List<TestCase> result = new ArrayList<TestCase>();
        for (TestCase testCase : testCases) {
            if (testCase.getTag() == null || testCase.getTag().size() == 0) {
                result.add(testCase);
            }
        }
        return result;
    }

    private boolean ignoreTestSuitTags(TestSuite testSuite) {
        List<String> tags = testSuite.getTags();
        return tags == null || tags.size() == 0;
    }

    private List<TestCase> filterCaseByTags(List<TestCase> testCases, boolean suiteHasTags) {
        List<TestCase> list = new ArrayList<TestCase>();
        for (TestCase testCase : testCases) {
            if (tagCanBeMatch(testCase.getTag(), suiteHasTags)) {
                list.add(testCase);
            }
        }
        return list;
    }

    private boolean tagCanBeMatch(List<String> actualTags, boolean suiteHasTags) {
        if ((actualTags == null || actualTags.size() == 0) && suiteHasTags) {
            return true;
        }
        TagExpression expression = new TagExpression(expectedTags);
        return expression.eval(actualTags);
    }
}
