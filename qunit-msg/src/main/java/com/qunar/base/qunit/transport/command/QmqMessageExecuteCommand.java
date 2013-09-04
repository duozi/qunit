package com.qunar.base.qunit.transport.command;

import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.model.ServiceDesc;
import com.qunar.base.qunit.objectfactory.BeanUtils;
import com.qunar.base.qunit.response.Response;
import com.qunar.base.qunit.transport.rpc.RpcServiceFactory;
import com.qunar.base.qunit.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qmq.service.ConsumerMessageHandler;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * User: zhaohuiyu
 * Date: 9/4/13
 * Time: 4:03 PM
 */
public class QmqMessageExecuteCommand extends ExecuteCommand {
    private static final Logger logger = LoggerFactory.getLogger(QmqMessageExecuteCommand.class);

    private final String subject;
    private final String consumerGroup;
    private final String host;

    public QmqMessageExecuteCommand(String id, String desc, String subject, String consumerGroup, String host) {
        super(id, desc);
        this.subject = subject;
        this.consumerGroup = consumerGroup;
        this.host = host;
    }

    @Override
    public Response execute(List<KeyValueStore> params) {
        Response response = new Response();
        try {
            com.qunar.base.qunit.transport.model.ServiceDesc desc =
                    new com.qunar.base.qunit.transport.model.ServiceDesc(ConsumerMessageHandler.class.getCanonicalName(),
                            "handle", host, "1.2.0", "");
            Object service = RpcServiceFactory.getRpcService(desc);
            Method executeMethod = ReflectionUtils.getMethod(desc.getMethod(), desc.getServiceClass());
            Object result = executeMethod.invoke(service,
                    BeanUtils.getParameters(params, executeMethod.getGenericParameterTypes()));
            response.setBody(result);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("illegal access", e);
        } catch (InvocationTargetException e) {
            logger.error("call rpc error", e.getTargetException());
            response.setException(e.getTargetException());
        }
        return response;
    }

    @Override
    public String toReport() {
        return String.format("给%s发送qmq消息，subject: %s, consumer group: %s",
                host,
                subject,
                consumerGroup);
    }

    @Override
    public ServiceDesc desc() {
        return new com.qunar.base.qunit.model.ServiceDesc(this.id,
                String.format("%s.%s", ConsumerMessageHandler.class.getCanonicalName(),
                        "handle"),
                this.desc);
    }
}
