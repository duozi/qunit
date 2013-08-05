package com.qunar.base.qunit.model;

import java.io.Serializable;
import java.util.Date;

/**
 * User: zhaohuiyu
 * Date: 7/11/12
 * Time: 4:01 PM
 */
public class FeedContent implements Serializable {

    private static final long serialVersionUID = 2001246602971362599L;

    /**
     * 用户ID
     */
    private Integer uid;

    /**
     * 接收用户ID(评论回复某人)
     */
    private Integer toUid;

    /**
     * feed原始id
     */
    private String feedOid;

    /**
     * feed内容
     */
    private String content;

    /**
     * feed类型
     */
    private Integer feedType;

    /**
     * 来源类型
     */
    private Integer originType;

    /**
     * feed时间
     */
    private Date feedTime;

    /**
     * feed来源ip
     */
    private Integer feedIp;

    public Integer getUid() {
        return uid;
    }

    public void setUid(Integer uid) {
        this.uid = uid;
    }

    public Integer getToUid() {
        return toUid;
    }

    public void setToUid(Integer toUid) {
        this.toUid = toUid;
    }


    public String getFeedOid() {
        return feedOid;
    }

    public void setFeedOid(String feedOid) {
        this.feedOid = feedOid;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }


    public Integer getFeedType() {
        return feedType;
    }

    public void setFeedType(Integer feedType) {
        this.feedType = feedType;
    }

    public Integer getOriginType() {
        return originType;
    }

    public void setOriginType(Integer originType) {
        this.originType = originType;
    }

    public Date getFeedTime() {
        return feedTime;
    }

    public void setFeedTime(Date feedTime) {
        this.feedTime = feedTime;
    }

    public Integer getFeedIp() {
        return feedIp;
    }

    public void setFeedIp(Integer feedIp) {
        this.feedIp = feedIp;
    }


}