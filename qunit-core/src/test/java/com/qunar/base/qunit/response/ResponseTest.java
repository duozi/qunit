package com.qunar.base.qunit.response;

import com.alibaba.fastjson.JSON;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.fail;

/**
 * User: zhaohuiyu
 * Date: 7/13/12
 * Time: 3:00 PM
 */
public class ResponseTest {
    @Test
    public void should_verify_xml_data() {
        Response response = new Response("<user></user>", null);

        Map<String, String> expected = new HashMap<String, String>();
        expected.put("xml", "<user></user>");
        response.verify(expected);
    }

    @Test
    public void should_verify_xml() {
        Response response = new Response("<user>abc</user>", null);

        Map<String, String> expected = new HashMap<String, String>();
        expected.put("xml", "<user></user>");

        try {
            response.verify(expected);
            fail("must different");
        } catch (Throwable e) {

        }
    }

    @Test
    public void should_verify_not_xml_data() {
        Response response = new Response("<user></user>", null);

        Map<String, String> expected = new HashMap<String, String>();
        expected.put("xml", "abc");
        try {
            response.verify(expected);
            fail("not xml will assert failed");
        } catch (Throwable e) {

        }
    }

    @Test
    public void should_contains_throwable_message_when_to_json() {
        Response response = new Response("<user></user>", new RuntimeException());
        String json = JSON.toJSONString(response);
        System.out.println(json);
        System.out.println(JSON.toJSONString(new RuntimeException()));
    }
}
