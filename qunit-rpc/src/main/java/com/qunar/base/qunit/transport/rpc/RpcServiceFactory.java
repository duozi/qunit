/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.transport.rpc;

import com.alibaba.dubbo.config.ApplicationConfig;
import com.alibaba.dubbo.config.ReferenceConfig;
import com.qunar.base.qunit.transport.exception.InitRpcServiceException;
import com.qunar.base.qunit.transport.model.ServiceDesc;
import com.qunar.base.qunit.util.PropertyUtils;
import com.qunar.base.qunit.util.ReflectionUtils;
import org.apache.commons.lang.StringUtils;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 远程服务工厂，用于初始化远程服务，获得对应服务的对象
 * <p/>
 * Created by JarnTang at 12-4-22 下午8:10
 *
 * @author <a href="mailto:changjiang.tang@qunar.com">JarnTang</a>
 */
public class RpcServiceFactory {

    private static final Map<String, Object> serviceCache = new ConcurrentHashMap<String, Object>();

    public static Object getRpcService(ServiceDesc serviceDesc) {
        String rpcUrl = getRpcServiceUrl(serviceDesc.getUrl());
        Class<?> interfaceClass = ReflectionUtils.loadClass(serviceDesc.getClazz());
        String cacheKey = serviceDesc.getCacheKey();
        Object service = serviceCache.get(cacheKey);
        if (service == null) {
            synchronized (serviceCache) {
                ApplicationConfig application = new ApplicationConfig();
                application.setName(PropertyUtils.getProperty("rpc.appName", "test"));
                ReferenceConfig<?> reference = new ReferenceConfig();
                reference.setApplication(application);
                reference.setInterface(interfaceClass);
                reference.setVersion(serviceDesc.getVersion());
                reference.setUrl(rpcUrl);
                if (StringUtils.isNotBlank(serviceDesc.getGroup())) {
                    reference.setGroup(serviceDesc.getGroup());
                }
                reference.setTimeout(Integer.valueOf(PropertyUtils.getProperty("rpc.timeout", "3000")));
                service = reference.get();
                serviceCache.put(cacheKey, service);
            }
        }
        return service;
    }

    private static String getRpcServiceUrl(String url) {
        StringBuilder sb = new StringBuilder(PropertyUtils.getProperty("rpc.protocol", "dubbo"));
        sb.append("://");
        sb.append(url);
        if (sb.indexOf(":") == -1) {
            sb.append(":").append("20880");
        }
        return sb.toString();
    }

}
