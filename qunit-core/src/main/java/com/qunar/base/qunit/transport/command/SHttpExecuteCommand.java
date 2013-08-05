package com.qunar.base.qunit.transport.command;

import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;
import com.qunar.base.qunit.transport.http.HttpService;

import java.util.List;
import java.util.Map;

/**
 * User: zhaohuiyu
 * Date: 3/29/13
 * Time: 4:47 PM
 */
public class SHttpExecuteCommand extends HttpExecuteCommand {
    public SHttpExecuteCommand(String id, String url, String method, String desc) {
        super(id, url, method, desc);
    }

    @Override
    public Response execute(List<KeyValueStore> params) {
        Map headers = getHttpHeaders(params);
        this.params = removedHttpHeaders(params);

        if (logger.isInfoEnabled()) {
            logger.info("Http request start: url={}, method={}, headers={}, params={}", new Object[]{url, method, headers, getParamsAsString(this.params)});
        }
        HttpService.setHeaders(headers);

        if (isEntityRequest(method)) {
            method = fixMethod(method);
            return HttpService.entityRequestWithStream(url, method, this.params);
        }

        Response response = HttpService.getWithStream(url, this.params);

        if (logger.isInfoEnabled()) {
            logger.info("Http Execute : method={}, url={}, params={}, response={}", new Object[]{method, url, getParamsAsString(params), response});
        }

        return response;
    }
}
