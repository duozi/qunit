package com.qunar.base.qunit.transport.command;

import com.alibaba.dubbo.common.URL;
import com.alibaba.fastjson.JSON;
import com.qunar.base.qunit.model.KeyValueStore;
import com.qunar.base.qunit.model.ServiceDesc;
import com.qunar.base.qunit.response.Response;
import com.qunar.base.qunit.transport.rpc.RpcServiceFactory;
import com.qunar.base.qunit.transport.zookeeper.*;
import com.qunar.base.qunit.util.PropertyUtils;
import com.qunar.base.qunit.util.ReflectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import qunar.tc.qmq.Message;
import qunar.tc.qmq.MessageProducer;
import qunar.tc.qmq.base.BaseMessage;
import qunar.tc.qmq.producer.MessageProducerProvider;
import qunar.tc.qmq.service.ConsumerMessageHandler;
import qunar.tc.qmq.utils.Constants;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * User: zhaohuiyu
 * Date: 9/4/13
 * Time: 4:03 PM
 */
public class QmqMessageExecuteCommand extends ExecuteCommand {
    private static final Logger logger = LoggerFactory.getLogger(QmqMessageExecuteCommand.class);

    private static Map<String, String> zkMap = new HashMap<String, String>(2);

    private static final Resolver resolver;

    private static final LoadBalance loadBalance = new RandomLoadBalance();

    static {
        zkMap.put("dev", "l-zk1.plat.dev.cn6.qunar.com:2181");
        zkMap.put("beta", "l-zk1.plat.beta.cn6:2181,l-zk2.plat.beta.cn6:2181,l-zk3.plat.beta.cn6:2181");

        String env = PropertyUtils.getProperty("test.env", "dev");
        ZKObserver observer = new ZKObserver(zkMap.get(env));
        resolver = new EventResolver();
        observer.onConsumerChanged(resolver);
        observer.start();
    }

    private final String subject;

    private final MessageProducer producer = new MessageProducerProvider("l-zk1.plat.dev.cn6.qunar.com:2181");

    public QmqMessageExecuteCommand(String id, String desc, String subject) {
        super(id, desc);
        this.subject = subject;
    }

    @Override
    public Response execute(List<KeyValueStore> params) {
        Response response = new Response();
        Map<String, Collection<Group>> list = resolver.resolve(subject);
        for (Map.Entry<String, Collection<Group>> entry : list.entrySet()) {
            String prefix = entry.getKey();
            for (Group group : entry.getValue()) {
                Endpoint endpoint = loadBalance.select(group);
                if (endpoint == null) continue;
                URL url = endpoint.getUrl();
                String host = url.getHost() + ":" + url.getPort();
                sendMessage(host, prefix, params, group);
            }
        }
        return response;
    }

    private void sendMessage(String host, String prefix, List<KeyValueStore> params, Group group) {
        try {
            com.qunar.base.qunit.transport.model.ServiceDesc desc =
                    new com.qunar.base.qunit.transport.model.ServiceDesc(ConsumerMessageHandler.class.getCanonicalName(),
                            "handle", host, "1.2.0", "");
            Object service = RpcServiceFactory.getRpcService(desc);
            Method executeMethod = ReflectionUtils.getMethod(desc.getMethod(), desc.getServiceClass());
            Message message = producer.generateMessage(subject);
            for (KeyValueStore param : params) {
                if (param.getName().equals(BaseMessage.keys.qmq_data.name())){
                    message.setData(JSON.parse(param.getValue().toString()));
                }else {
                    message.setProperty(param.getName(), param.getValue().toString());
                }
            }
            ((BaseMessage) message).setProperty(BaseMessage.keys.qmq_brokerGroupName, Constants.DEFAULT_GROUP);
            ((BaseMessage) message).setProperty(BaseMessage.keys.qmq_prefix, prefix);
            ((BaseMessage) message).setProperty(BaseMessage.keys.qmq_consumerGroupName, group.getName());
            logger.info("发送的消息为:{}", message);

            executeMethod.invoke(service, message);
        } catch (IllegalAccessException e) {
            logger.error("access error", e);
        } catch (InvocationTargetException e) {
            logger.error("send message error", e.getTargetException());
        }
    }

    @Override
    public String toReport() {
        return String.format("给%s发送qmq消息，subject: %s",
                PropertyUtils.getProperty("test.env", "dev"),
                subject);
    }

    @Override
    public ServiceDesc desc() {
        return new com.qunar.base.qunit.model.ServiceDesc(this.id,
                String.format("%s.%s", ConsumerMessageHandler.class.getCanonicalName(),
                        "handle"),
                this.desc);
    }
}
