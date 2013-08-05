package com.qunar.base.qunit.objectfactory;

import org.hamcrest.core.Is;
import org.junit.Test;

import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 8/22/12
 * Time: 12:36 PM
 */
public class EnumFactoryTest {
    @Test
    public void should_create_enum() {
        EnumFactory factory = new EnumFactory();
        Object result = factory.create(Status.class, "YES");
        assertThat((Status) result, Is.is(Status.YES));
    }

    private static enum Status {
        YES, NO
    }
}
