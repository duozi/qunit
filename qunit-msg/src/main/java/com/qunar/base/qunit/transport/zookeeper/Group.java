package com.qunar.base.qunit.transport.zookeeper;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;

/**
 * @author miao.yang susing@gmail.com
 * @date 2012-12-27
 */
public class Group implements Iterable<Endpoint> {

    private final String name;

    private final CopyOnWriteArraySet<Endpoint> endpoints = new CopyOnWriteArraySet<Endpoint>();

    Group(String name) {
        this.name = name;
    }

    void add(Endpoint endpoint) {
        endpoints.add(endpoint);
    }

    void remove(Endpoint endpoint) {
        endpoints.remove(endpoint);
    }

    public int size() {
        return endpoints.size();
    }

    @Override
    public Iterator<Endpoint> iterator() {
        return endpoints.iterator();
    }

    public Endpoint get(int index) {
        int i = 0;
        for (Endpoint endpoint : endpoints) {
            if (i++ == index) return endpoint;
        }
        return null;
    }

    public String getName() {
        return name;
    }
}