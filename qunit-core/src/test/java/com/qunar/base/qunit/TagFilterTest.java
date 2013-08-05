package com.qunar.base.qunit;

import com.qunar.base.qunit.casefilter.TagFilter;
import com.qunar.base.qunit.model.TestCase;
import com.qunar.base.qunit.model.TestSuite;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 11/2/12
 */
public class TagFilterTest {
    @Test
    public void should_include_all_cases_given_expected_tag_is_star() {
        TagFilter filter = new TagFilter(asList("*"));
        TestSuite testSuite = new TestSuite();
        TestCase testCase = new TestCase();
        testCase.setTag(asList("slow"));
        testSuite.setTestCases(asList(testCase));
        testSuite.setTag(asList("slow"));

        filter.filter(testSuite);

        assertThat(testSuite.getTestCases().size(), is(1));
    }

    @Test
    public void should_include_TestCases_given_entryPoint_and_suite_with_tags_but_TestCase_without_tags() {
        TagFilter filter = new TagFilter(asList("automated"));
        TestSuite testSuite = new TestSuite();
        TestCase testCase = new TestCase();
        testSuite.setTestCases(asList(testCase));
        testSuite.setTag(asList("automated"));

        filter.filter(testSuite);

        assertThat(testSuite.getTestCases().size(), is(1));
    }

    @Test
    public void should_not_include_any_TestCases_given_entryPoint_with_tags_but_suite_and_case_without_tags() {
        TagFilter filter = new TagFilter(asList("automated"));
        TestSuite testSuite = new TestSuite();
        TestCase testCase = new TestCase();
        testSuite.setTestCases(asList(testCase));

        filter.filter(testSuite);

        assertThat(testSuite.getTestCases().size(), is(0));
    }

    @Test
    public void should_not_concern_tags_on_TestCase_given_tags_on_TestSuite_not_match() {
        TagFilter filter = new TagFilter(asList("slow"));
        TestSuite testSuite = new TestSuite();
        TestCase testCase = new TestCase();
        testCase.setTag(asList("slow"));
        testSuite.setTestCases(asList(testCase));
        testSuite.setTag(asList("quick"));

        filter.filter(testSuite);

        assertThat(testSuite.getTestCases().size(), is(0));
    }

    @Test
    public void should_not_include_TestCase_given_tag_not_match() {
        TagFilter filter = new TagFilter(asList("slow"));
        TestSuite testSuite = new TestSuite();
        TestCase testCase1 = new TestCase();
        testCase1.setTag(asList("slow"));
        testCase1.setId("test case1");
        TestCase testCase2 = new TestCase();
        testCase2.setTag(asList("quick"));
        testSuite.setTestCases(asList(testCase1, testCase2));
        testSuite.setTag(asList("slow"));

        filter.filter(testSuite);

        assertThat(testSuite.getTestCases().size(), is(1));
        assertThat(testSuite.getTestCases().get(0).getId(), is("test case1"));
    }

    @Test
    public void should_include_all_given_tags() {
        TagFilter filter = new TagFilter(asList("slow", "quick"));
        TestSuite testSuite = new TestSuite();
        TestCase testCase1 = new TestCase();
        testCase1.setTag(asList("slow"));
        testCase1.setId("test case1");
        TestCase testCase2 = new TestCase();
        testCase2.setTag(asList("quick"));

        testSuite.setTestCases(asList(testCase1, testCase2));
        testSuite.setTag(asList("slow"));

        filter.filter(testSuite);

        assertThat(testSuite.getTestCases().size(), is(2));
    }

    @Test
    public void should_support_tags_and_relation() {
        TagFilter filter = new TagFilter(asList("reviewed & p1"));
        TestSuite testSuite = new TestSuite();
        TestCase testCase1 = new TestCase();
        testCase1.setTag(asList("reviewed", "p1"));
        testCase1.setId("test case1");
        testSuite.setTestCases(asList(testCase1));

        filter.filter(testSuite);

        assertThat(testSuite.getTestCases().size(), is(1));
    }

    @Test
    public void should_not_include_TestCase_given_part_match() {
        TagFilter filter = new TagFilter(asList("reviewed & p1"));
        TestSuite testSuite = new TestSuite();
        TestCase testCase1 = new TestCase();
        testCase1.setTag(asList("reviewed"));
        testCase1.setId("test case1");
        testSuite.setTestCases(asList(testCase1));

        filter.filter(testSuite);

        assertThat(testSuite.getTestCases().size(), is(0));
    }

    @Test
    public void should_use_full_match_for_expectedTags_contain_and() {
        TagFilter filter = new TagFilter(asList("reviewed&p1", "reviewed&p2", "passed&p1", "passed&p2"));
        TestSuite testSuite = new TestSuite();
        TestCase testCase1 = new TestCase();
        testCase1.setTag(asList("reviewed", "p1"));
        testCase1.setId("test case1");

        TestCase testCase2 = new TestCase();
        testCase2.setTag(asList("reviewed", "p2"));
        testCase2.setId("test case1");

        TestCase testCase3 = new TestCase();
        testCase3.setTag(asList("reviewed", "p3"));
        testCase3.setId("test case1");

        TestCase testCase4 = new TestCase();
        testCase4.setTag(asList("passed", "p1"));
        testCase4.setId("test case1");

        TestCase testCase5 = new TestCase();
        testCase5.setTag(asList("passed", "p2"));
        testCase5.setId("test case1");

        TestCase testCase6 = new TestCase();
        testCase6.setTag(asList("passed", "p3"));
        testCase6.setId("test case1");

        TestCase testCase7 = new TestCase();
        testCase7.setTag(asList("dev", "p1"));
        testCase7.setId("test case1");

        TestCase testCase8 = new TestCase();
        testCase8.setTag(asList("dev", "p2"));
        testCase8.setId("test case1");

        TestCase testCase9 = new TestCase();
        testCase9.setTag(asList("dev", "p3"));
        testCase9.setId("test case1");

        testSuite.setTestCases(asList(testCase1, testCase2, testCase3,
                testCase4, testCase5, testCase6, testCase7, testCase8, testCase9));

        filter.filter(testSuite);

        assertThat(testSuite.getTestCases().size(), is(4));

    }

}
