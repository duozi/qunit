package com.qunar.base.qunit.transport.zookeeper;

import org.apache.commons.lang.StringUtils;

/**
 * User: zhaohuiyu
 * Date: 1/9/13
 * Time: 10:35 AM
 */
public abstract class AbstractLoadBalance implements LoadBalance {
    @Override
    public Endpoint select(Group group, String host) {
        if (group.size() == 0) return null;
        if (StringUtils.isNotBlank(host)) {
            for (Endpoint endpoint : group) {
                if (host.equals(endpoint.getUrl().getHost())) return endpoint;
            }
            return null;
        }
        if (group.size() == 1) {
            return group.get(0);
        }
        return doSelect(group);
    }

    protected abstract Endpoint doSelect(Group group);
}
