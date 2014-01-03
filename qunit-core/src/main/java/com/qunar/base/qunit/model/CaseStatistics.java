package com.qunar.base.qunit.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * User: zonghuang
 * Date: 12/19/13
 */
public class CaseStatistics implements Serializable{

    private static final long serialVersionUID = -8000260006371137545L;

    private String job;

    private String build;

    private int sum;

    private int runSum;

    private int success;

    private int failed;

    private List<String> failedIdList = new ArrayList<String>();

    public int getFailed() {
        return failed;
    }

    public void addFailed(String id) {
        failed++;
        failedIdList.add(id);
    }

    public void addSuccess() {
        success++;
    }

    public int getRunSum() {
        return runSum;
    }

    public void setRunSum(int runSum) {
        this.runSum = runSum;
    }

    public int getSuccess() {
        return success;
    }

    public void setSuccess(int success) {
        this.success = success;
    }

    public int getSum() {
        return sum;
    }

    public void addSum(int num) {
        sum += num;
    }

    public void addRunSum(int num) {
        runSum += num;
    }

    public List<String> getFailedIdList() {
        return failedIdList;
    }

    public String getBuild() {
        return build;
    }

    public void setBuild(String build) {
        this.build = build;
    }

    public String getJob() {
        return job;
    }

    public void setJob(String job) {
        this.job = job;
    }
}
