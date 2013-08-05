/*
* $$Id$$
* Copyright (c) 2011 Qunar.com. All Rights Reserved.
*/
package com.qunar.base.qunit.service;

import com.qunar.base.qunit.model.User;

public class UserService {

    public User[] register(User user) {
        return new User[]{user};
    }

    public String hello() {
        return "123456";
    }

    public User logon(String name, String password) {
        User user = new User();
        user.setName(name);
        user.setPassword(password);
        return user;
    }

    public String getUserAsXml() {
        return "<user>" +
                "<name>admin</name>" +
                "<pwd>12345</pwd>" +
                "</user>";
    }
}
