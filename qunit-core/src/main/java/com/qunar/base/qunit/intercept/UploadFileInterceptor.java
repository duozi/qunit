package com.qunar.base.qunit.intercept;

import com.qunar.base.qunit.command.StepCommand;
import com.qunar.base.qunit.context.Context;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.response.Response;
import com.qunar.base.qunit.transport.http.HttpService;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UploadFileInterceptor extends ParameterInterceptor {
    private static final String CONTENT_TYPE = "Content-Type";
    private static final String HTTP_HEADERS = "http-headers";

    @Override
    protected List<KeyValueStore> convert(List<KeyValueStore> params) {
        List<KeyValueStore> parameters = removedHeaders(params);
        FileUploadWriter fileUploadWriter = new FileUploadWriter();
        fileUploadWriter.write(parameters);

        KeyValueStore httpHeaders = newHttpHeaders(params, fileUploadWriter);

        List<KeyValueStore> result = new ArrayList<KeyValueStore>();
        result.add(httpHeaders);
        result.add(new KeyValueStore("param", fileUploadWriter.toByteArray()));
        IOUtils.closeQuietly(fileUploadWriter);
        return result;
    }

    @Override
    public Object afterExecute(StepCommand command, Response response, Context context) {
        HttpService.removeHeader(CONTENT_TYPE);
        return response;
    }

    private KeyValueStore newHttpHeaders(List<KeyValueStore> params, FileUploadWriter fileUploadWriter) {
        KeyValueStore httpHeaders = extractHttpHeaders(params);
        Map headers = (Map) httpHeaders.getValue();
        headers.put(CONTENT_TYPE, "multipart/form-data; boundary=" + fileUploadWriter.getBoundary());
        return httpHeaders;
    }

    @Override
    protected boolean support(List<KeyValueStore> params) {
        KeyValueStore httpHeaders = extractHttpHeaders(params);
        return isMultipart(httpHeaders);
    }

    private boolean isMultipart(KeyValueStore httpHeaders) {
        if (httpHeaders == null) return false;
        Map headers = (Map) httpHeaders.getValue();
        Object contentType = headers.get(CONTENT_TYPE);
        if (contentType == null) return false;
        String contentTypeStr = contentType.toString();
        if (!StringUtils.startsWith(contentTypeStr, "multipart/form-data")) return false;
        return true;
    }

    private List<KeyValueStore> removedHeaders(List<KeyValueStore> commandParameters) {
        List<KeyValueStore> result = new ArrayList<KeyValueStore>();
        for (KeyValueStore commandParameter : commandParameters) {
            if (!commandParameter.getName().equalsIgnoreCase(HTTP_HEADERS)) {
                result.add(commandParameter);
            }
        }
        return result;
    }

    private KeyValueStore extractHttpHeaders(List<KeyValueStore> commandParameters) {
        for (KeyValueStore parameter : commandParameters) {
            if (parameter.getName().equals(HTTP_HEADERS)) return parameter;
        }
        return null;
    }
}
