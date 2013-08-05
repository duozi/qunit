package com.qunar.base.qunit.transport.command;

import com.qunar.autotest.mock.util.JSON;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.model.ServiceDesc;
import com.qunar.base.qunit.objectfactory.BeanUtils;
import com.qunar.base.qunit.response.Response;
import com.qunar.base.qunit.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.List;

/**
 * User: zhaohuiyu
 * Date: 5/30/12
 * Time: 12:43 PM
 */
public class LocalExecuteCommand extends ExecuteCommand {
    private final static Logger logger = LoggerFactory.getLogger(LocalExecuteCommand.class);

    private String clazz;
    private String method;

    public LocalExecuteCommand(String id, String clazz, String method, String desc) {
        super(id, desc);
        this.clazz = clazz;
        this.method = method;
    }

    @Override
    public Response execute(List<KeyValueStore> params) {
        Response response = new Response();
        Object service = ReflectionUtils.newInstance(ReflectionUtils.loadClass(clazz));
        Method executeMethod = ReflectionUtils.getMethod(service, method);
        Type[] genericParameterTypes = executeMethod.getGenericParameterTypes();
        try {
            Object[] parameters = BeanUtils.getParameters(params, genericParameterTypes);
            logger.info("调用方法{}.{},参数:{}", new Object[]{clazz, method, JSON.toJSONString(parameters)});
            Object result = executeMethod.invoke(service, parameters);
            response.setBody(result);
        } catch (IllegalArgumentException e) {
            logger.error("参数处理错误", e);
            throw new RuntimeException(e);
        } catch (Exception e) {
            logger.error("本地方法执行错误 " + e.getMessage(), e);
            response.setException(e.getCause());
        }
        return response;
    }

    @Override
    public String toReport() {
        return String.format("调用本地类%s的方法%s", this.clazz, this.method);
    }

    @Override
    public ServiceDesc desc() {
        return new ServiceDesc(this.id, String.format("%s.%s", this.clazz, this.method), this.desc);
    }

}
