package com.qunar.base.qunit.model;

import com.qunar.base.qunit.util.*;

public class Group {
    private int id;

    private String name;

    public com.qunar.base.qunit.util.User[] getUsers() {
        return users;
    }

    public void setUsers(com.qunar.base.qunit.util.User[] users) {
        this.users = users;
    }

    private com.qunar.base.qunit.util.User[] users;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
