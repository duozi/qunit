package com.qunar.base.qunit.model;

/**
 * User: zhaohuiyu
 * Date: 2/16/13
 * Time: 10:47 AM
 */
public class ServiceDesc {
    /*
    服务id
     */
    private String id;

    /*
    服务
     */
    private String service;

    /*
    服务描述
     */
    private String desc;

    /*
    服务调用次数
     */
    private Integer count;


    public Integer getSuccessCount() {
        return successCount;
    }

    public void setSuccessCount(Integer successCount) {
        this.successCount = successCount;
    }

    public Long getTotalSuccessDuration() {
        return totalSuccessDuration;
    }

    public void setTotalSuccessDuration(Long totalSuccessDuration) {
        this.totalSuccessDuration = totalSuccessDuration;
    }

    private Integer successCount;

    private Long totalSuccessDuration;

    public ServiceDesc(String id, String service, String desc) {
        this.id = id;
        this.service = service;
        this.desc = desc;
        this.count = 0;
        this.successCount = 0;
        this.totalSuccessDuration = 0L;
    }

    public void called() {
        ++count;
    }

    public String getId() {
        return id;
    }

    public String getService() {
        return service;
    }

    public String getDesc() {
        return desc;
    }

    public Integer getCount() {
        return count;
    }

    public void callSuccess() {
        ++successCount;
    }

    public void addDuration(Long duration) {
        this.totalSuccessDuration += duration;
    }
}
