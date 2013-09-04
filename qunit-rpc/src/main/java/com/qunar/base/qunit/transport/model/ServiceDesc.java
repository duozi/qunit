package com.qunar.base.qunit.transport.model;

import com.qunar.base.qunit.util.PropertyUtils;
import com.qunar.base.qunit.util.ReflectionUtils;
import org.apache.commons.lang.StringUtils;

/**
 * User: zhaohuiyu
 * Date: 12/11/12
 */
public class ServiceDesc {
    private String className;

    private String method;

    private String url;

    private String version;

    private String group;

    public ServiceDesc(String className, String method, String url, String version, String group) {
        this.className = className;
        this.method = method;
        this.url = url;
        this.version = version;
        this.group = group;
    }

    public String getMethod() {
        return method;
    }

    public String getVersion() {
        version = StringUtils.isBlank(this.version) ? PropertyUtils.getProperty("rpc.version", "1.0") : version;
        return version;
    }

    public String getUrl() {
        return url;
    }

    public String getClazz() {
        return className;
    }

    public Class getServiceClass() {
        return ReflectionUtils.loadClass(this.className);
    }

    public String getGroup() {
        return this.group;
    }

    public String getCacheKey() {
        StringBuilder sb = new StringBuilder();
        sb.append("url=").append(url).append("&");
        sb.append("interface=")
                .append(className)
                .append("&");
        if (StringUtils.isNotBlank(getVersion())) {
            sb.append("version=").append(getVersion()).append("&");
        }
        if (StringUtils.isNotBlank(group)) {
            sb.append("group=").append(group).append("&");
        }

        return sb.toString();
    }
}
