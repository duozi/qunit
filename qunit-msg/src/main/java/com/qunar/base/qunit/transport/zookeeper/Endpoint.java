/*
 * Copyright (c) 2012 Qunar.com. All Rights Reserved.
 */
package com.qunar.base.qunit.transport.zookeeper;

import com.alibaba.dubbo.common.URL;
import org.apache.commons.lang3.ObjectUtils;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * @author miao.yang susing@gmail.com
 * @date 2012-12-27
 */
public class Endpoint implements Comparable<Endpoint> {

    private static final ConcurrentHashMap<String, Endpoint> cache = new ConcurrentHashMap<String, Endpoint>();

    public static Endpoint getInstance(String address) {
        Endpoint ep = cache.get(address);
        if (ep == null) {
            ep = new Endpoint(address);
            ep = ObjectUtils.defaultIfNull(cache.putIfAbsent(address, ep), ep);
        }
        return ep;
    }

    private final URL url;

    /*
    以下可用于每次请求之后更新，然后做复杂均衡
     */
    private final AtomicInteger success = new AtomicInteger();
    private final AtomicInteger error = new AtomicInteger();

    private transient long lastAccess = 0;
    private transient long lastError = 0;

    private Endpoint(String url) {
        String decodeUrl = URL.decode(url);
        this.url = URL.valueOf(decodeUrl);
    }

    public URL getUrl() {
        return this.url;
    }

    @Override
    public int compareTo(Endpoint o) {
        return 0;

    }

    @Override
    public int hashCode() {
        return url.hashCode() + 31;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Endpoint other = (Endpoint) obj;
        if (url == null) {
            if (other.url != null)
                return false;
        } else if (!url.equals(other.url))
            return false;
        return true;
    }


}
