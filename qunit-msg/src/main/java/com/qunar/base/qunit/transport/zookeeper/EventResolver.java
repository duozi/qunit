package com.qunar.base.qunit.transport.zookeeper;

import com.alibaba.dubbo.common.URL;
import com.google.common.eventbus.Subscribe;

import java.util.Collection;
import java.util.Map;

/**
 * User: zhaohuiyu
 * Date: 4/1/13
 * Time: 2:08 PM
 */
public class EventResolver implements Resolver {

    private final ChannelSelector channelSelector;

    public EventResolver() {
        this.channelSelector = new ChannelSelector();
    }

    @Override
    public Map<String, Collection<Group>> resolve(String subject) {
        return channelSelector.select(subject);
    }

    @Override
    public Group findGroup(String prefix, String groupName) {
        return channelSelector.findGroup(prefix, groupName);
    }

    @Subscribe
    public void handle(NewConsumerEvent event) {
        String subject = event.getSubject();
        String ip = URL.valueOf(URL.decode(event.getAddress())).getHost();

            channelSelector.addChannel(event.getPrefix(),
                    event.getGroup(),
                    event.getAddress());

    }

    @Subscribe
    public void handle(RemovedConsumerEvent event) {
        channelSelector.removeChannel(event.getPrefix(),
                event.getGroup(),
                event.getAddress());
    }

    @Subscribe
    public void handle(NewGroupEvent event) {
        channelSelector.addGroup(event.getPrefix(), event.getGroup());
    }

    @Subscribe
    public void handle(RemovedGroupEvent event) {
        channelSelector.removeGroup(event.getPrefix(), event.getGroup());
    }
}
