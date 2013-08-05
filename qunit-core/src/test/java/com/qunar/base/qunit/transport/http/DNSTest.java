package com.qunar.base.qunit.transport.http;

import org.junit.Test;

import java.net.MalformedURLException;
import java.net.URL;

/**
 * User: zhaohuiyu
 * Date: 9/27/12
 */
public class DNSTest {
    @Test(expected = MalformedURLException.class)
    public void should_throw_exception_given_url_without_protocol_prefix() throws MalformedURLException {
        URL url = new URL("localhost:80/test.json");
    }
}
