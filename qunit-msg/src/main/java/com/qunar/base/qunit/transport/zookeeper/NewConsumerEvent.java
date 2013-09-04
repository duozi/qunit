package com.qunar.base.qunit.transport.zookeeper;

/**
 * User: zhaohuiyu
 * Date: 4/2/13
 * Time: 7:35 PM
 */
public class NewConsumerEvent extends ConsumerEvent {

    public NewConsumerEvent(String subject, String group, String address) {
        super(subject, group, address);
    }
}
