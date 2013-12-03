/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.transport.http;

import com.qunar.autotest.CaseIdHolder;
import com.qunar.base.meerkat.http.QunarHttpClient;
import com.qunar.base.meerkat.http.data.PostParameter;
import com.qunar.base.qunit.exception.UnsupportedFilterResultType;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;
import com.qunar.base.qunit.util.PropertyUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.entity.AbstractHttpEntity;
import org.apache.http.entity.ByteArrayEntity;
import org.apache.http.entity.StringEntity;
import org.apache.http.params.CoreProtocolPNames;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.qunar.base.meerkat.http.QunarHttpClient.createDefaultClient;
import static org.apache.commons.lang.StringUtils.*;

/**
 * 负责HTTP调用的服务
 * <p/>
 * Created by JarnTang at 12-5-19 下午6:38
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class HttpService {
    private final static Logger logger = LoggerFactory.getLogger(HttpService.class);

    public static QunarHttpClient httpClient;
    static int connTimeout = 2000;
    static int readTimeout = 6000;
    static int maxTotal = 200;
    static int maxPerRoute = 50;
    private static Map headers;
    private static final String CONTENT_TYPE = "Content-Type";


    static {
        httpClient = createDefaultClient(connTimeout, readTimeout, maxTotal, maxPerRoute);

        httpClient.setRequestGlobalCookies();
        httpClient.allowCookiePolicy();
        HttpParams params = httpClient.getParams();
        params.setParameter("http.socket.timeout", 600000);
        params.setParameter("http.connection.stalecheck", true);
        if ("true".equals(PropertyUtils.getProperty("enableRedirect", "true"))){
        	params.setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.TRUE);
        } else {
            params.setParameter(ClientPNames.HANDLE_REDIRECTS, Boolean.FALSE);
        }
        httpClient.getParams().setParameter(CoreProtocolPNames.PROTOCOL_VERSION, httpVersion());
    }

    public static Response get(String url, List<KeyValueStore> params) {
        HttpResponse httpResponse = doGet(url, params);
        return response(httpResponse);
    }

    public static Response getWithStream(String url, List<KeyValueStore> params) {
        HttpResponse httpResponse = doGet(url, params);
        return responseWithStream(httpResponse);
    }

    public static Response entityRequest(String url, String method, List<KeyValueStore> params) {
        HttpRequestBase request = null;
        try {
            request = doEntityRequest(url, method, params);
            HttpResponse httpResponse = httpClient.execute(request);
            return response(httpResponse);
        } catch (Exception e) {
            if (request != null) {
                request.abort();
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }
    
    public static HttpResponse getHttpResponse(String url, String method, List<KeyValueStore> params){
    	HttpRequestBase request = null;
        try {
            request = doEntityRequest(url, method, params);
            return httpClient.execute(request);
        } catch (Exception e) {
            if (request != null) {
                request.abort();
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public static Response entityRequestWithStream(String url, String method, List<KeyValueStore> params) {
        HttpRequestBase request = null;
        try {
            request = doEntityRequest(url, method, params);
            HttpResponse httpResponse = httpClient.execute(request);
            return responseWithStream(httpResponse);
        } catch (Exception e) {
            if (request != null) {
                request.abort();
            }
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    private static Response responseWithStream(HttpResponse httpResponse) {
        int statusCode = httpResponse.getStatusLine().getStatusCode();
        InputStream content = null;
        try {
            content = httpResponse.getEntity().getContent();
            return new com.qunar.base.qunit.response.HttpResponse(statusCode, content, httpResponse.getAllHeaders());
        } catch (IOException e) {
            IOUtils.closeQuietly(content);
            throw new RuntimeException("读取网络流出错");
        }
    }

    private static HttpVersion httpVersion() {
        String httpVersion = PropertyUtils.getProperty("http.version", "1.1");
        logger.info("Use http version {}", httpVersion);
        if (httpVersion.equals("1.1")) {
            return HttpVersion.HTTP_1_1;
        } else {
            return HttpVersion.HTTP_1_0;
        }
    }

    private static com.qunar.base.qunit.response.HttpResponse response(HttpResponse httpResponse) {
        return new com.qunar.base.qunit.response.HttpResponse(
                httpResponse.getStatusLine().getStatusCode(),getContent(httpResponse), httpResponse.getAllHeaders());
    }

    private static HttpRequestBase doEntityRequest(String url, final String method, List<KeyValueStore> params) throws Exception {
        AbstractHttpEntity entity = null;
        if (isBodyEntity(params)) {
            logger.info("params: {}", params);
            Object entityBody = getEntityBody(params);
            logger.info("entityBody: {}", entityBody.toString());
            if (isByteArray(entityBody)) {
                entity = new ByteArrayEntity(getByteArray((Object[]) entityBody));
            } else if (isString(entityBody)) {
                entity = new StringEntity((String) entityBody, HTTP.UTF_8);
            } else {
                throw new UnsupportedFilterResultType(String.format("filter result type %s is unsupported.", entityBody.getClass().getCanonicalName()));
            }
            HttpRequestBase request = doEntityRequest(url, method, entity);
            Header[] headers = request.getHeaders(CONTENT_TYPE);
            if (headers.length == 0) {
                request.setHeader(CONTENT_TYPE, "application/json");
            }
            return request;
        } else {
            PostParameter postParameter = getParameters(convertRequestParameter(params));
            entity = new UrlEncodedFormEntity(postParameter.getNvps(), HTTP.UTF_8);
            return doEntityRequest(url, method, entity);
        }
    }

    public static byte[] getByteArray(Object[] entityBody) {
        byte[] result = new byte[entityBody.length];
        for (int i = 0; i < entityBody.length; i++) {
            result[i] = (Byte) entityBody[i];
        }
        return result;
    }

    public static boolean isString(Object value) {
        return (value != null) && (value instanceof String);
    }

    public static boolean isByteArray(Object value) {
        if ((value != null) && (value.getClass().isArray())) {
            int length = Array.getLength(value);
            for (int i = 0; i < length; i++) {
                Object o = Array.get(value, i);
                if (!(o instanceof Byte)) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    public static Object getEntityBody(List<KeyValueStore> params) {
        if (params != null) {
            for (KeyValueStore kvs : params) {
                if ("param".equals(kvs.getName())) {
                    return kvs.getValue();
                }
            }
        }
        return null;
    }

    public static boolean isBodyEntity(List<KeyValueStore> params) {
        return !(params == null || params.size() != 1) && containsKey(params, "param");
    }

    public static boolean containsKey(List<KeyValueStore> params, String param) {
        for (KeyValueStore kvs : params) {
            if (param.equals(kvs.getName())) {
                return true;
            }
        }
        return false;
    }

    private static HttpRequestBase doEntityRequest(String url, final String method, AbstractHttpEntity entity) {
        HttpEntityEnclosingRequestBase request = new HttpEntityEnclosingRequestBase() {
            @Override
            public String getMethod() {
                return method.toUpperCase();
            }
        };
        String host = DNS.getHost(url);
        url = urlWithIp(url);
        request.setURI(URI.create(url));
        request.setHeader("Host", host);
        setHeaders(request);
        setCaseIdToHeader(request);
        request.setEntity(entity);
        return request;
    }

    private static void setCaseIdToHeader(HttpRequestBase request) {
        String caseId = CaseIdHolder.get();
        String logHostUrl = PropertyUtils.getProperty("autotest-log.host");
        if (caseId != null && StringUtils.isNotBlank(logHostUrl)) {
            request.setHeader(CaseIdHolder.CASEIDLABEL, CaseIdHolder.get());
        }
    }

    public static void setHeaders(HttpRequestBase request) {
        if (headers != null && headers.size() > 0) {
            for (Object key : headers.keySet()) {
                if (key == null || headers.get(key) == null) {
                    continue;
                }
                request.setHeader(key.toString(), headers.get(key).toString());
            }
        }
    }

    public static HttpResponse doGet(String url, List<KeyValueStore> params) {
        HttpGet httpGet = null;
        try {
            String host = DNS.getHost(url);
            url = urlWithIp(url);
            httpGet = new HttpGet(getHttpGetURL(url, params));
            httpGet.setHeader("Host", host);
            setHeaders(httpGet);
            setCaseIdToHeader(httpGet);
            return httpClient.execute(httpGet);
        } catch (IOException e) {
            if (httpGet != null) {
                httpGet.abort();
            }
            throw new RuntimeException(String.format("访问(%s)出错", url), e);
        }
    }

    public static String getContent(HttpResponse httpResponse) {
        try {
            HttpEntity entity = httpResponse.getEntity();
            return EntityUtils.toString(entity, "UTF-8");
        } catch (IOException e) {
            throw new RuntimeException("读取网络流出错", e);
        }
    }

    protected static String getHttpGetURL(String url, List<KeyValueStore> parameters) {
        if (parameters == null || isEmpty(url)) {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        sb.append(url);
        if (!endsWith(url, "?")) {
            sb.append("?");
        }
        for (KeyValueStore kvs : parameters) {
            String name = kvs.getName();
            String value = encode((String) kvs.getValue());
            sb.append(name).append("=").append(value).append("&");
        }
        String result = sb.toString();
        if (result.endsWith("&")) {
            result = result.substring(0, result.length() - 1);
        }
        return result;
    }

    public static String urlWithIp(String url) {
        if (StringUtils.isBlank(url)) return url;
        String host = DNS.getHost(url);
        String ip = DNS.dnsLookup(host);
        if (!host.equalsIgnoreCase(ip))
            url = url.replace(host, ip);
        return fixUrlPrefix(url);
    }

    private static String fixUrlPrefix(String url) {
        boolean hasPrefix = startsWithAny(lowerCase(url), new String[]{"http://", "https://"});
        if (!hasPrefix) {
            logger.warn("Your url should with a protocol:{}", url);
            return String.format("http://%s", url);
        }
        return url;
    }

    private static String encode(String value) {
        try {
            if (value != null) {
                return URLEncoder.encode(value, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e.getMessage(), e);
        }
        return null;
    }

    private static Map<String, List<String>> convertRequestParameter(List<KeyValueStore> parameters) {
        Map<String, List<String>> param = new HashMap<String, List<String>>();
        if (parameters != null && !parameters.isEmpty()) {
            for (KeyValueStore kvs : parameters) {
                List<String> list = param.get(kvs.getName());
                if (list == null) {
                    list = new ArrayList<String>();
                    list.add((String) kvs.getValue());
                    param.put(kvs.getName(), list);
                } else {
                    list.add((String) kvs.getValue());
                }
            }
        }
        return param;
    }

    /**
     * 获取请求参数
     *
     * @param parameters 请求参数
     * @return 封装后的POST类型参数
     */
    public static PostParameter getParameters(Map<String, List<String>> parameters) {
        PostParameter postParameter = new PostParameter();
        if (parameters == null || parameters.isEmpty()) {
            return postParameter;
        }
        for (Map.Entry<String, List<String>> entry : parameters.entrySet()) {
            for (String value : entry.getValue()) {
                postParameter.put(entry.getKey(), value);
            }
        }
        return postParameter;
    }

    public static void setHeaders(Map headers) {
        if (headers != null) {
            HttpService.headers = headers;
        }
    }

    public static void removeHeader(String header) {
        if (StringUtils.isBlank(header)) return;
        if (headers == null || headers.size() <= 0) return;
        for (Object key : headers.keySet()) {
            if (key == null) continue;
            String keyStr = key.toString();

            if (keyStr.equals(header)) {
                headers.remove(header);
                return;
            }
        }
    }
}
