/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.transport.command;

import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.model.ServiceDesc;
import com.qunar.base.qunit.response.Response;
import com.qunar.base.qunit.transport.http.HttpService;
import org.apache.commons.lang.StringUtils;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * HTTP类型的请求执行器,负责http请求的执行
 * <p/>
 * Created by JarnTang at 12-5-20 下午3:59
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class HttpExecuteCommand extends ExecuteCommand {

    protected final static Logger logger = LoggerFactory.getLogger(HttpExecuteCommand.class);
    private final static String SPLIT = "|";

    protected String url;
    protected String method;
    protected List<KeyValueStore> params;

    public HttpExecuteCommand(String id, String url, String method, String desc) {
        super(id, desc);
        this.url = url;
        this.method = method;
    }

    @Override
    public Response execute(List<KeyValueStore> params) {
        //List<KeyValueStore> processParams = splitParameters(params);
        Map headers = getHttpHeaders(params);
        this.params = removedHttpHeaders(params);

        if (logger.isInfoEnabled()) {
            logger.info("Http request start: url={}, method={}, headers={}, params={}", new Object[]{url, method, headers, getParamsAsString(this.params)});
        }
        HttpService.setHeaders(headers);

        Response response = null;
        if (isEntityRequest(method)) {
            method = fixMethod(method);
            response = HttpService.entityRequest(url, method, this.params);
        } else {
            response = HttpService.get(url, this.params);
        }

        /*if (XSSService.checkXss(url, params, method)) {
            fail("该接口可能存在xss攻击");
            return response;
        } else {
            if (logger.isInfoEnabled()) {
                logger.info("Http Execute : method={}, url={}, params={}, response={}", new Object[]{method, url, getParamsAsString(params), response});
            }
            return response;
        }*/
        if (logger.isInfoEnabled()) {
            logger.info("Http Execute : method={}, url={}, params={}, response={}", new Object[]{method, url, getParamsAsString(params), response});
        }
        return response;
    }

    private static List<KeyValueStore> splitParameters(List<KeyValueStore> params){
        if (params == null) {
            return params;
        }
        List<KeyValueStore> result= new ArrayList<KeyValueStore>(params.size());
        for (KeyValueStore param : params) {
            Object value = param.getValue();
            if ((value instanceof String) && ((String) value).contains(SPLIT)) {
                String[] valueArray = StringUtils.split((String)value, SPLIT);
                for (int i = 0; i < valueArray.length; i++) {
                    result.add(new KeyValueStore(param.getName(), valueArray[i].trim()));
                }
            } else {
                result.add(param);
            }
        }
        return result;
    }

    protected String fixMethod(String method) {
        if ("binary".equalsIgnoreCase(method)) {
            return "POST";
        }
        return method;
    }

    protected boolean isEntityRequest(String method) {
        return HttpPost.METHOD_NAME.equalsIgnoreCase(method)
                || HttpPut.METHOD_NAME.equalsIgnoreCase(method)
                || "binary".equalsIgnoreCase(method);
    }

    protected Map getHttpHeaders(List<KeyValueStore> params) {
        for (KeyValueStore param : params) {
            if (isHttpHeaders(param)) {
                return (Map) param.getValue();
            }
        }
        return null;
    }

    protected String getParamsAsString(List<KeyValueStore> params) {
        if (params == null) {
            return "";
        }
        StringBuilder sb = new StringBuilder();
        for (KeyValueStore kv : params) {
            sb.append(kv.getName()).append("=").append(kv.getValue()).append("&");
        }

        return sb.toString();
    }

    protected List<KeyValueStore> removedHttpHeaders(List<KeyValueStore> params) {
        if (params == null) {
            return null;
        }
        List<KeyValueStore> result = new ArrayList<KeyValueStore>();
        for (KeyValueStore param : params) {
            if (isHttpHeaders(param)) continue;
            result.add(param);
        }
        return result;
    }

    protected boolean isHttpHeaders(KeyValueStore param) {
        return param.getName().equalsIgnoreCase("http-headers");
    }

    protected String showReportUrlWithLink(String url, List<KeyValueStore> params) {
        if (!"get".equals(this.method)) return url;
        StringBuilder urlBuffer = new StringBuilder();
        urlBuffer.append("<a href=\"").append(url).append("?");
        if (params != null && !params.isEmpty()) {
            for (KeyValueStore param : params) {
                urlBuffer.append(param.getName()).append("=").append(param.getValue()).append("&");
            }
            urlBuffer.deleteCharAt(urlBuffer.lastIndexOf("&"));
        }
        urlBuffer.append("\">").append(url).append("</a>");
        return urlBuffer.toString();
    }

    @Override
    public String toReport() {
        return String.format("使用%s方式调用HTTP接口%s", StringUtils.isBlank(this.method) ? "get" : this.method,
                showReportUrlWithLink(url, this.params));
    }

    @Override
    public ServiceDesc desc() {
        return new ServiceDesc(this.id, this.url, this.desc);
    }

}
