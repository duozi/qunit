package com.qunar.base.qunit.transport.zookeeper;

/**
 * User: zhaohuiyu
 * Date: 4/17/13
 * Time: 11:14 AM
 */
public class RemovedGroupEvent extends GroupEvent {
    public RemovedGroupEvent(String prefix, String group) {
        super(prefix, group);
    }
}
