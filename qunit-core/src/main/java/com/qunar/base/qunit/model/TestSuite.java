package com.qunar.base.qunit.model;

import org.apache.commons.lang.StringUtils;

import java.util.*;

public class TestSuite extends MappedElement implements Comparable<TestSuite> {

    private String id;
    private String desc;
    private List<String> tags;
    private String caseFileName;
    private List<TestCase> testCases;
    private List<TestCase> backGrounds;
    private List<TestCase> beforeSuite;
    private List<TestCase> afterSuite;
    private List<TestCase> beforeCase;
    private List<TestCase> afterCase;

    public String getId() {
        if (StringUtils.isBlank(id)) return this.desc;
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public List<TestCase> getTestCases() {
        return testCases;
    }

    public void setTestCases(List<TestCase> testCases) {
        this.testCases = testCases;
    }

    public String getCaseFileName() {
        return caseFileName;
    }

    public void setCaseFileName(String caseFileName) {
        this.caseFileName = caseFileName;
    }

    public List<TestCase> getBackGrounds() {
        if (backGrounds == null) {
            backGrounds = new ArrayList<TestCase>();
        }
        return backGrounds;
    }

    public void setBackGrounds(List<TestCase> backGrounds) {
        this.backGrounds = backGrounds;
    }

    public String getDesc() {
        if (StringUtils.isBlank(desc)) return this.id;
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTag(List<String> tags) {
        this.tags = tags;
    }

    public List<TestCase> getBeforeSuite() {
        return beforeSuite;
    }

    public void setBeforeSuite(List<TestCase> beforeSuite) {
        this.beforeSuite = beforeSuite;
    }

    public List<TestCase> getAfterSuite() {
        return afterSuite;
    }

    public void setAfterSuite(List<TestCase> afterSuite) {
        this.afterSuite = afterSuite;
    }

    public List<TestCase> getBeforeCase() {
        return beforeCase;
    }

    public void setBeforeCase(List<TestCase> beforeCase) {
        this.beforeCase = beforeCase;
    }

    public List<TestCase> getAfterCase() {
        return afterCase;
    }

    public void setAfterCase(List<TestCase> afterCase) {
        this.afterCase = afterCase;
    }

    @Override
    public Map asMap() {
        Map<Object, Object> suiteMap = new HashMap<Object, Object>();
        suiteMap.put("id", getId());
        suiteMap.put("name", getDesc());
        suiteMap.put("keyword", "Feature");
        suiteMap.put("uri", getCaseFileName());
        suiteMap.put("comments", "");
        suiteMap.put("tags", getTags(tags));
        return suiteMap;
    }

    public TestSuite clone() {
        TestSuite testSuite = new TestSuite();
        testSuite.setDesc(desc);
        testSuite.setId(id);
        testSuite.setCaseFileName(caseFileName);
        testSuite.setTag(cloneTag(tags));
        testSuite.setTestCases(cloneCommand(testCases));
        testSuite.setAfterCase(cloneCommand(afterCase));
        testSuite.setAfterSuite(cloneCommand(afterSuite));
        testSuite.setBackGrounds(cloneCommand(backGrounds));
        testSuite.setBeforeCase(cloneCommand(beforeCase));
        testSuite.setBeforeSuite(cloneCommand(beforeSuite));

        return testSuite;
    }

    private List<TestCase> cloneCommand(List<TestCase> testCaseList) {
        if (testCaseList != null) {
            List<TestCase> testCases = new ArrayList<TestCase>();
            for (TestCase testCase : testCaseList) {
                if (testCase instanceof DataDrivenTestCase) {
                    testCases.add(((DataDrivenTestCase)testCase).clone((DataDrivenTestCase) testCase));
                } else {
                    testCases.add(testCase.clone());
                }
            }
            return testCases;
        }
        return Collections.EMPTY_LIST;
    }

    private List<String> cloneTag(List<String> tagList) {
        if (tags != null) {
            List<String> tags = new ArrayList<String>();
            for (String tag : tags) {
                tags.add(tag);
            }
            return tags;
        }
        return Collections.EMPTY_LIST;
    }

    @Override
    public int compareTo(TestSuite testSuite) {
        return getDesc().compareTo(testSuite.getDesc());
    }
}