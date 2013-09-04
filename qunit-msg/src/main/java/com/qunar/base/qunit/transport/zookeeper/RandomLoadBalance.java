package com.qunar.base.qunit.transport.zookeeper;

import java.util.Random;

/**
 * User: zhaohuiyu
 * Date: 1/8/13
 * Time: 4:01 PM
 */
public class RandomLoadBalance extends AbstractLoadBalance {

    private final Random random = new Random();

    @Override
    public Endpoint doSelect(Group group) {
        int size = group.size();

        int index = random.nextInt(size);

        return group.get(index);
    }
}
