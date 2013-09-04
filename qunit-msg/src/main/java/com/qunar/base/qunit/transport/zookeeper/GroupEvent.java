package com.qunar.base.qunit.transport.zookeeper;

/**
 * User: zhaohuiyu
 * Date: 4/17/13
 * Time: 11:14 AM
 */
public class GroupEvent {
    private String prefix;
    private String group;

    public GroupEvent(String prefix, String group) {
        this.prefix = prefix;
        this.group = group;
    }

    public String getPrefix() {
        return prefix;
    }

    public String getGroup() {
        return group;
    }
}
