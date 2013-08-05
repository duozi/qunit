/**
 * $$Id$$
 * Copyright (c) 2011 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.transport.command;

import com.qunar.base.qunit.model.KeyValueStore;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

/**
 * 描述：
 * Created by JarnTang at 12-7-16 下午7:51
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class HttpExecuteCommandTest {

    HttpExecuteCommand command;

    @Before
    public void setUp() throws Exception {
        command = new HttpExecuteCommand("1234", "http://www.qunar.com", "get", "test");

    }

    @Test
    public void should_return_url_and_parameter_when_has_parameter() {
        ArrayList<KeyValueStore> params = new ArrayList<KeyValueStore>();
        params.add(new KeyValueStore("key1", "value1"));
        params.add(new KeyValueStore("key2", "value2"));
        String result = command.showReportUrlWithLink("http://www.qunar.com", params);
        assertThat(result, containsString("key2=value2"));
        assertThat(result, containsString("key1=value1"));
    }

    @Test
    public void should_return_url_and_parameter_when_not_has_parameter() {
        String result = command.showReportUrlWithLink("http://www.qunar.com", new ArrayList<KeyValueStore>());
        assertThat(result, is("<a href=\"http://www.qunar.com?\">http://www.qunar.com</a>"));
    }

}
