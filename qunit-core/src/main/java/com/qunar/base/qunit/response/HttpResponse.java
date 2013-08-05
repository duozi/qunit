package com.qunar.base.qunit.response;

import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;

import java.io.InputStream;
import java.util.Map;

import static org.hamcrest.core.Is.is;
import static org.hamcrest.core.StringContains.containsString;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * User: zhaohuiyu
 * Date: 5/30/12
 * Time: 2:36 PM
 */
public class HttpResponse extends Response {
    private Integer status;
    private String callback;
    private Header[] headers;

    public HttpResponse(Integer status, String body, Header[] headers) {
        super(body, null);
        this.status = status;
        this.headers = headers;
    }

    public HttpResponse(Integer status, String body) {
        super(body, null);
        this.status = status;
    }

    public HttpResponse(Integer status, InputStream body, Header[] headers) {
        super(body, null);
        this.status = status;
        this.headers = headers;
    }

    @Override
    public void verify(Map<String, String> expected) {
        String expectedStatus = expected.get("status");
        assertStatus(expectedStatus);
        assertCallbackFunctionName(expected.get("callback"));
        assertHeaders(expected);
        super.verify(expected);
    }

    private void assertHeaders(Map<String, String> expectedHeaders) {
        for (Map.Entry<String, String> entry : expectedHeaders.entrySet()) {
            String header = entry.getKey();
            if (!header.startsWith("header-")) continue;
            String headerName = header.substring("header-".length());
            String expected = entry.getValue();
            String actual = getHeaderContent(headerName);

            if (StringUtils.isNotBlank(actual)) {
                assertThat("http header " + headerName, actual, containsString(expected));
            } else if (!expected.equalsIgnoreCase(actual)) {
                fail("期望存在http header " + headerName);
            }
        }

    }

    private void assertCallbackFunctionName(String expectCallback) {
        if (StringUtils.isNotBlank(expectCallback)) {
            assertThat("js callback function name is not equals.", this.callback, is(expectCallback));
        }
    }

    private void assertStatus(String expectedStatus) {
        if (StringUtils.isNotBlank(expectedStatus)) {
            assertThat("http request status is not equals.", this.status.toString(), is(expectedStatus));
        }
    }

    private String getHeaderContent(String headerName){
        if (headers == null) return StringUtils.EMPTY;
        for (int i = 0; i < headers.length; i++){
            if (headerName.equalsIgnoreCase(headers[i].getName())){
                return headers[i].getValue();
            }
        }
        return StringUtils.EMPTY;
    }

    public void setCallback(String callback) {
        this.callback = callback;
    }

    public String getCallback() {
        return callback;
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "HttpResponse{" + "status=" + status + ", body='" + body + '\'' + '}';
    }
}
