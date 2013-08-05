package com.qunar.base.qunit.util;

import org.junit.Test;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 7/13/12
 * Time: 6:51 PM
 */
public class RegexTest {
    private final static Pattern pattern = Pattern.compile("\\{([A-Za-z]+)\\}");

    @Test
    public void should_extract_dollr_variable() {
        Matcher matcher = pattern.matcher("http://www.qunar.com/blog/{bloger}/{articleId}.json");
        ArrayList<String> result = extractVariables(matcher);

        assertThat(result.size(), is(2));
        assertThat(result, hasItem("bloger"));
        assertThat(result, hasItem("articleId"));
    }

    @Test
    public void should_extract_complex() {
        Matcher matcher = pattern.matcher("http://www.qunar.com/blog/{year}-{month}.json");
        ArrayList<String> result = extractVariables(matcher);

        assertThat(result.size(), is(2));
        assertThat(result, hasItem("year"));
        assertThat(result, hasItem("month"));
    }

    private ArrayList<String> extractVariables(Matcher matcher) {
        ArrayList<String> result = new ArrayList<String>();
        while (matcher.find()) {
            result.add(matcher.group(1));
        }
        return result;
    }

}
