package com.qunar.base.qunit.transport.zookeeper;

/**
 * User: zhaohuiyu
 * Date: 1/9/13
 * Time: 10:35 AM
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public Endpoint select(Group group) {
        if (group.size() == 0) return null;
        if (group.size() == 1) {
            return group.get(0);
        }
        return doSelect(group);
    }

    protected abstract Endpoint doSelect(Group group);
}
