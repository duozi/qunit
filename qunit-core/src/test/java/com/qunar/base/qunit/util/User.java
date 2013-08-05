package com.qunar.base.qunit.util;


public class User {
    private String userName;

    private Address address;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public void setAddress(Address address, String i) {
    }

    public void setAddress(Address address, Integer i) {
    }

    public void setAddress(Address address, int i) {
    }

    public void setAddress(String i, Address address) {
    }
}