package com.qunar.base.qunit.objectfactory;

/**
 * User: zhaohuiyu
 * Date: 11/13/12
 */
public class User {
    private final String name;
    private final String age;

    private final Address address;

    public User(String name, String age, Address address){
        this.name = name;
        this.age = age;
        this.address = address;
    }

    public String getName() {
        return name;
    }
}
