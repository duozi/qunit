package com.qunar.base.qunit.transport.zookeeper;

import java.util.Collection;
import java.util.Map;

/**
 * User: zhaohuiyu
 * Date: 12/26/12
 * Time: 3:49 PM
 */
public interface Resolver {
	
    Map<String, Collection<Group>> resolve(String subject);
    
    Group findGroup(String prefix, String groupName);
}
