package com.qunar.base.qunit.model;

/**
 * User: zhaohuiyu
 * Date: 7/13/12
 * Time: 10:56 AM
 */
public class MultipleCtorWithDiffType {
    private String a;
    private Integer b;
    private String c;

    public MultipleCtorWithDiffType(String a, String c) {
        this.a = a;
        this.c = c;
    }

    public MultipleCtorWithDiffType(String a, Integer b) {
        this.a = a;
        this.b = b;
    }

    public String getA() {
        return a;
    }

    public Integer getB() {
        return b;
    }

    public String getC() {
        return c;
    }
}
