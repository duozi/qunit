package com.qunar.base.qunit.transport.zookeeper;

/**
 * User: zhaohuiyu
 * Date: 4/3/13
 * Time: 11:21 AM
 */
public class RemovedConsumerEvent extends ConsumerEvent {

    public RemovedConsumerEvent(String subject, String group, String address) {
        super(subject, group, address);
    }
}
