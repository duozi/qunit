package com.qunar.base.qunit.matchers;

import com.qunar.base.qunit.model.Group;
import org.hamcrest.Matcher;
import org.junit.Test;

import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 6/8/12
 * Time: 10:43 AM
 */
public class HamcrestMatcherWrapperTest {
    @Test
    public void should_use_containsString() {
        HamcrestMatcherWrapper hamcrestMatcherWrapper = new HamcrestMatcherWrapper("containsString");
        Matcher matche = hamcrestMatcherWrapper.matches("test");

        assertThat("test test",matche);
    }

    @Test
    public void should_compare_object() {
        HamcrestMatcherWrapper is = new HamcrestMatcherWrapper("is");
        Group group = new Group();
        group.setId(1);
        group.setName("test");
        Matcher matcher = is.matches(group);

        assertThat(group,matcher);
    }


}
