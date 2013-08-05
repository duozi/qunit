package com.qunar.base.qunit.model;

/**
 * User: zhaohuiyu
 * Date: 7/12/12
 * Time: 6:32 PM
 */
public class UserCookies {
    private String pCookie;
    private String vCookie;
    private String qCookie;

    public UserCookies(String pCookie, String vCookie, String qCookie) {
        this.pCookie = pCookie;
        this.vCookie = vCookie;
        this.qCookie = qCookie;
    }

    public String getpCookie() {
        return pCookie;
    }

    public String getvCookie() {
        return vCookie;
    }

    public String getqCookie() {
        return qCookie;
    }
}
