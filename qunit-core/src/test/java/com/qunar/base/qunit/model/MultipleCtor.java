package com.qunar.base.qunit.model;

/**
 * User: zhaohuiyu
 * Date: 7/13/12
 * Time: 10:32 AM
 */
public class MultipleCtor {
    private String d;

    public MultipleCtor(String a, String b, String c) {
        this.a = a;
        this.b = b;
        this.c = c;
    }

    public MultipleCtor(String a, String b, String c, String d) {
        this(a, b, c);
        this.d = d;
    }

    private String a;
    private String b;
    private String c;

    public String getA() {
        return a;
    }

    public String getB() {
        return b;
    }

    public String getC() {
        return c;
    }
}
