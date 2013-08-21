package com.qunar.base.qunit.model;

import java.util.List;

/**
 * User: zonghuang
 * Date: 8/16/13
 */
public class DataCase {

    private String id;

    private String desc;

    private String level;

    private String status;

    private String executor;

    private String follow;

    private List<DataCase> caseChain;

    public DataCase(String id, String desc, String level, String status, String executor, String follow) {
        this.desc = desc;
        this.executor = executor;
        this.id = id;
        this.level = level;
        this.status = status;
        this.follow = follow;
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

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getExecutor() {
        return executor;
    }

    public void setExecutor(String executor) {
        this.executor = executor;
    }

    public String getFollow() {
        return follow;
    }

    public void setFollow(String follow) {
        this.follow = follow;
    }

    public List<DataCase> getCaseChain() {
        return caseChain;
    }

    public void setCaseChain(List<DataCase> caseChain) {
        this.caseChain = caseChain;
    }
}
