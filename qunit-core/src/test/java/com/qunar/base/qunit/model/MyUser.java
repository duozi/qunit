package com.qunar.base.qunit.model;

import com.qunar.base.qunit.util.*;

/**
 * User: zhaohuiyu
 * Date: 6/10/12
 * Time: 6:29 PM
 */
public class MyUser extends com.qunar.base.qunit.util.User {

    private String myname;

    private String no;

    public String getNo() {
        return no;
    }

    public void setNo(String no) {
        this.myname = no;
        this.no = no;
    }

    public String getMyname() {
        return myname;
    }

    public void setMyname(String myname) {
        this.myname = myname;
    }
}
