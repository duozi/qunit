package com.qunar.base.qunit;

import com.qunar.base.qunit.casefilter.TagExpression;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Collection;
import java.util.List;

import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 11/2/12
 */
@RunWith(Parameterized.class)
public class TagExpressionTest {

    private List<String> expected;
    private final List<String> actual;
    private final boolean match;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {
        return asList(
                new Object[][]{
                        //  expected(入口点上)     actual(case中)           是否运行
                        {asList("p1 & p2 & p3"), asList("p1", "p2", "p3"), TRUE},
                        {asList("p1 | p2 | p3"), asList("p1"), TRUE},
                        {asList("passed & !p3 & !p4"), asList("passed", "p3"), FALSE},
                        {asList("passed | review & !p3 & !p4"), asList("passed"), TRUE},
                        {asList("passed | review & !p3 & !p4"), asList("passed","p3"), FALSE},
                        {asList("passed | review & !p3 & !p4"), asList("passed","p1"), TRUE},
                        {asList("passed | review & !p3 & !p4"), asList("dev","p1"), FALSE},
                        {asList("passed | review & !p3 & !p4"), asList("dev"), FALSE},
                        {asList("passed | review & !p3 & !p4"), asList("dev","p3"), FALSE},
                        {asList("passed & !p3 | !p4"), asList("passed", "p3"), TRUE}
                }
        );
    }

    public TagExpressionTest(List<String> expected, List<String> actual, boolean match) {
        this.expected = expected;
        this.actual = actual;
        this.match = match;
    }

    @Test
    public void test_tag_expression() {
        TagExpression expression = new TagExpression(this.expected);

        assertThat(expression.eval(this.actual), is(this.match));
    }
}
