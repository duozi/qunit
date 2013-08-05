package com.qunar.base.qunit.transport.command;

import com.caucho.hessian.client.HessianProxyFactory;
import com.qunar.autotest.mock.util.JSON;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.model.ServiceDesc;
import com.qunar.base.qunit.objectfactory.BeanUtils;
import com.qunar.base.qunit.response.Response;
import com.qunar.base.qunit.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: zhaohuiyu
 * Date: 5/6/13
 * Time: 12:26 PM
 */
public class HessianExecuteCommand extends ExecuteCommand {
    private static final Logger logger = LoggerFactory.getLogger(HessianExecuteCommand.class);

    private HessianProxyFactory factory = new HessianProxyFactory();

    private static final Map<String, Object> hessianProxyMap = new HashMap<String, Object>();

    private String url;
    private String interfaceName;
    private String methodName;

    public HessianExecuteCommand(String id, String url, String interfaceName, String method, String desc) {
        super(id, desc);
        this.url = url;
        this.interfaceName = interfaceName;
        this.methodName = method;
        factory.setDebug(false);
        factory.setOverloadEnabled(true);
    }

    @Override
    public Response execute(List<KeyValueStore> params) {
        Response response = new Response();
        Class serviceClass = ReflectionUtils.loadClass(this.interfaceName);
        Method method = ReflectionUtils.getMethod(this.methodName, serviceClass);
        Type[] realParameters = method.getGenericParameterTypes();
        try {
            Object proxy = createProxy(serviceClass);
            Object[] parameters = BeanUtils.getParameters(params, realParameters);
            if (logger.isInfoEnabled()) {
                logger.info("Http request start: url={}, interface={}, method={}, params={}",
                        new Object[]{url, interfaceName, methodName, JSON.toJSONString(parameters)});
            }
            Object result = method.invoke(proxy, parameters);
            response.setBody(result);
        } catch (IllegalArgumentException e) {
            throw new RuntimeException("调用参数出现异常", e);
        } catch (MalformedURLException e) {
            logger.error("wrong url", e);
            throw new RuntimeException("wrong url", e);
        } catch (InvocationTargetException e) {
            response.setException(e.getTargetException());
        } catch (IllegalAccessException e) {
            logger.error("illegal access error", e);
            throw new RuntimeException("illegal access error", e);
        }
        return response;
    }

    private Object createProxy(Class serviceClass) throws MalformedURLException {
        String key = String.format("%s-%s", this.url, this.interfaceName);
        Object proxy = hessianProxyMap.get(key);
        if (proxy != null) {
            return proxy;
        }
        Object service = factory.create(serviceClass, this.url);
        hessianProxyMap.put(key, service);
        return service;
    }


    @Override
    public String toReport() {
        return String.format("调用位于%s的Hessian服务%s.%s", this.url, this.interfaceName, this.methodName);
    }

    @Override
    public ServiceDesc desc() {
        return new com.qunar.base.qunit.model.ServiceDesc(this.id,
                String.format("%s.%s", this.interfaceName, this.methodName),
                this.desc);
    }
}
