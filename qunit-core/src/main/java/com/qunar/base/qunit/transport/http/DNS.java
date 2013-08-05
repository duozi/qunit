package com.qunar.base.qunit.transport.http;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.apache.commons.lang.StringUtils.startsWith;
import static org.apache.commons.lang.StringUtils.trim;

/**
 * User: zhaohuiyu
 * Date: 9/27/12
 */
public class DNS {
    private static final Logger logger = LoggerFactory.getLogger(DNS.class);

    private final static Map<String, String> DOMAIN2IP = new HashMap<String, String>();
    private static final String DEFAULT_PROTOCOL_PREFIX = "http://";

    static {
        loadCustomHosts();
    }

    private static void loadCustomHosts() {
        InputStream stream = HttpService.class.getClassLoader().getResourceAsStream("hosts");
        if (stream == null) return;
        try {
            List<String> lines = IOUtils.readLines(stream);
            for (String line : lines) {
                if (isComment(line)) continue;
                String[] items = StringUtils.split(line, " ");
                if (items.length < 2) continue;
                for (int i = 1; i < items.length; ++i) {
                    if (isComment(items[i])) continue;
                    DOMAIN2IP.put(trim(items[i]), trim(items[0]));
                }
            }
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        } finally {
            IOUtils.closeQuietly(stream);
        }
    }

    private static boolean isComment(String line) {
        return startsWith(trim(line), "#");
    }

    public static String dnsLookup(String domain) {
        String ip = DOMAIN2IP.get(domain);
        if (StringUtils.isBlank(ip)) return domain;
        return ip;
    }

    public static String getHost(String uri) {
        URL url = null;
        try {
            if (!startsWith(uri, DEFAULT_PROTOCOL_PREFIX)) {
                uri = DEFAULT_PROTOCOL_PREFIX + uri;
            }
            url = new URL(uri);
        } catch (MalformedURLException e) {
            return null;
        }
        return url.getHost();
    }
}
