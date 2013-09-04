package com.qunar.base.qunit.transport.zookeeper;

/**
 * User: zhaohuiyu
 * Date: 4/3/13
 * Time: 11:22 AM
 */
public class ConsumerEvent {
    private String subject;
    private String group;
    private String address;

    public ConsumerEvent(String subject, String group, String address) {
        this.subject = subject;
        this.group = group;
        this.address = address;
    }

    public String getPrefix() {
        return subject;
    }

    public String getGroup() {
        return group;
    }

    public String getAddress() {
        return address;
    }

	public String getSubject() {
		return subject;
	}
    
}
