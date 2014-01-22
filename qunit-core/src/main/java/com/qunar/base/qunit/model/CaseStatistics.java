package com.qunar.base.qunit.model;

import java.io.Serializable;

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

    private StringBuffer failedDescBuffer = new StringBuffer();

    public int getFailed() {
        return failed;
    }

    public void addFailed(String desc) {
        failed++;
        failedDescBuffer.append(desc).append(";");
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

    public String getFailedIdList() {
        return failedDescBuffer.toString();
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
