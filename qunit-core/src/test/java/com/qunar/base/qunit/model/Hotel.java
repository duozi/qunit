package com.qunar.base.qunit.model;

import java.util.HashMap;
import java.util.Map;

public class Hotel {
    private String[] seqs;

    private Map<String,String> attr = new HashMap<String, String>();

    public Map<String, String> getAttr() {
        return attr;
    }

    public String[] getSeqs() {
        return seqs;
    }
}
