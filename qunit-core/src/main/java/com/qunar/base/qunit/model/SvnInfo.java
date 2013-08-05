package com.qunar.base.qunit.model;

import org.apache.commons.lang.StringUtils;

public class SvnInfo {
    private String url = StringUtils.EMPTY;
    private String reversion = StringUtils.EMPTY;

    public SvnInfo(String url, String reversion) {
        this.url = url;
        this.reversion = reversion;
    }

    public SvnInfo() {

    }

    public String getUrl() {
        return url;
    }

    public String getReversion() {
        return reversion;
    }
}
