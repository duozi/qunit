package com.qunar.base.qunit.transport.http;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.qunar.base.qunit.model.KeyValueStore;

public class XSSService {
	private final static Logger logger = LoggerFactory.getLogger(XSSService.class);
	
	private final static String XSS_RANDOM = ">'qwstf-xss-%s\"<";
	
	public static boolean checkXss(String url, List<KeyValueStore> params, String method) {
		if (CollectionUtils.isEmpty(params)){
			return false;
		}
		Random rand = new Random();
		int randomNum = rand.nextInt(100);
		String additional = String.format(XSS_RANDOM, randomNum);
		
		if (isEntityRequest(method)){
			try {
				return checkOtherMethod(url, params, method, additional);
			} catch (Exception e) {
				logger.error("check xss with other Method error:", e);
				return false;
			}
		}else {   
			return checkGetMethod(url, params, additional);
		}
	}
	
	private static boolean checkGetMethod(String url, List<KeyValueStore> params, String additional){
		for (int i = 0; i < params.size(); i++) {
			List<KeyValueStore> tempList = copyList(params);
			setParams(tempList, i, additional);
			HttpResponse httpResponse = HttpService.doGet(url, params);
			if (getResult(httpResponse, additional)){
				return true;
			}
		}
		
		return false;
	}
	
	private static boolean checkOtherMethod(String url, List<KeyValueStore> params, String method, String additional) throws Exception{
		method = fixMethod(method);
		for (int i = 0; i < params.size(); i++){
			List<KeyValueStore> tempList = copyList(params);
			setParams(tempList, i, additional);
			HttpResponse httpResponse = HttpService.getHttpResponse(url, method, params);
			if (getResult(httpResponse, additional)){
				return true;
			}
		}
		return false;
	}
	
	private static List<KeyValueStore> copyList(List<KeyValueStore> params){
		List<KeyValueStore> copyList = new ArrayList<KeyValueStore>();
		for (KeyValueStore kvs : params){
			copyList.add(kvs);
		}
		return copyList;
	}
	
	private static void setParams(List<KeyValueStore> params, int index, String additional){
		for (int j = 0; j < params.size(); j++) {
			if( j == index){
				params.get(j).setValue(params.get(j).getValue() + additional);
			}
		}
	}
	
	private static String fixMethod(String method) {
        if ("binary".equalsIgnoreCase(method)) {
            return "POST";
        }
        return method;
    }
	
	private static boolean isEntityRequest(String method) {
        return HttpPost.METHOD_NAME.equalsIgnoreCase(method)
                || HttpPut.METHOD_NAME.equalsIgnoreCase(method)
                || "binary".equalsIgnoreCase(method);
    }
	
	private static boolean getResult(HttpResponse httpResponse, String randomStr){
		String header = getHeader(httpResponse);
		String body = HttpService.getContent(httpResponse);
		if(httpResponse.getStatusLine().getStatusCode() == 200 && checkHeader(header)){
			if (StringUtils.isNotBlank(body) && body.contains(randomStr)){
				return true;
			}
		}
		return false;
	}
	
	private static String getHeader(HttpResponse httpResponse){
		Header[] headers = httpResponse.getAllHeaders();
		if (headers != null && headers.length != 0){
			for (Header header : headers){
				if ("content-type".equals(header.getName().toLowerCase())){
					return header.getValue();
				}
			}
		}
		
		return "";
	}
	
	private static boolean checkHeader(String header){
		if (StringUtils.isEmpty(header)){
			return true;
		}
		if (header.contains("text/html") || header.contains("text/plain")){
			return true;
		}
		String[] array = header.split(";");
		if (array.length == 1 && array[0].contains("charset")){
			return true;
		}
		return false;
	}

}
