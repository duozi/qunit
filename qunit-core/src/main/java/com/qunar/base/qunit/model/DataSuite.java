package com.qunar.base.qunit.model;

import java.util.List;
import java.util.Map;

/**
 * User: zonghuang
 * Date: 8/16/13
 */
public class DataSuite {

    private String id;

    private String desc;

    private String caseFileName;

    private Map<String, DataCase> dataCases;

    public String getCaseFileName() {
        return caseFileName;
    }

    public void setCaseFileName(String caseFileName) {
        this.caseFileName = caseFileName;
    }

    public Map<String, DataCase> getDataCases() {
        return dataCases;
    }

    public void setDataCases(Map<String, DataCase> dataCases) {
        this.dataCases = dataCases;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
