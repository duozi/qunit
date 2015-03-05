package com.qunar.base.qunit.transport.zookeeper;

/**
 * User: zhaohuiyu
 * Date: 1/8/13
 * Time: 3:55 PM
 */
public interface LoadBalance {
    Endpoint select(Group group, String host);
}
