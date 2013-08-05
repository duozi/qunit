package com.qunar.base.qunit.objectfactory;

import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * User: zhaohuiyu
 * Date: 10/9/12
 */
public class CharFactoryTest {
    @Test
    public void should_create_char_from_string() {
        CharFactory factory = new CharFactory();
        Object actual = factory.create(char.class, "t");

        assertThat((Character) actual, is('t'));

    }
}
