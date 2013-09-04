/*
 * Copyright (c) 2012 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.transport.zookeeper;

import com.google.common.collect.Maps;
import com.googlecode.concurrenttrees.common.KeyValuePair;
import com.googlecode.concurrenttrees.radix.node.concrete.DefaultCharArrayNodeFactory;
import org.apache.commons.lang3.ObjectUtils;
import qunar.tc.qmq.utils.PrefixMatcher;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

/**
 * @author miao.yang susing@gmail.com
 * @date 2012-12-27
 */
class ChannelSelector {

    private final PrefixMatcher<ConcurrentMap<String, Group>> tree = new PrefixMatcher<ConcurrentMap<String, Group>>(new DefaultCharArrayNodeFactory());

    public void addChannel(String prefix, String groupName, String address) {
        Group group = addGroup(prefix, groupName);
        group.add(Endpoint.getInstance(address));
    }

    public Group addGroup(String prefix, String groupName) {
        ConcurrentMap<String, Group> map = tree.getValueForExactKey(prefix);
        if (map == null) {
            map = new ConcurrentHashMap<String, Group>();
            map = ObjectUtils.defaultIfNull(tree.putIfAbsent(prefix, map), map);
        }

        Group group = map.get(groupName);
        if (group == null) {
            group = new Group(groupName);
            group = ObjectUtils.defaultIfNull(map.putIfAbsent(groupName, group), group);
        }
        return group;
    }

    public void removeChannel(String prefix, String groupName, String address) {
        ConcurrentMap<String, Group> map = tree.getValueForExactKey(prefix);
        if (map == null)
            return;

        Group group = map.get(groupName);
        if (group == null)
            return;

        group.remove(Endpoint.getInstance(address));
    }

    public void removeGroup(String prefix, String groupName) {
        ConcurrentMap<String, Group> map = tree.getValueForExactKey(prefix);
        if (map == null) {
            return;
        }

        Group group = map.get(groupName);
        if (group == null) {
            return;
        }
        if (group.size() != 0) {
            return;
        }
        map.remove(groupName);
    }

    public Map<String, Collection<Group>> select(String subject) {
        Map<String, Collection<Group>> map = Maps.newHashMap();
        for (KeyValuePair<ConcurrentMap<String, Group>> entry : tree.getKeyValuePairsForKeysPrefixIn(subject)) {
            map.put(entry.getKey().toString(), entry.getValue().values());
        }
        return map;
    }

    public Group findGroup(String prefix, String groupName) {
        ConcurrentMap<String, Group> map = tree.getValueForExactKey(prefix);
        if (map == null)
            return null;

        return map.get(groupName);
    }
}
