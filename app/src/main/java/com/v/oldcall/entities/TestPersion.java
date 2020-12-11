package com.v.oldcall.entities;

/**
 * Author:v
 * Time:2020/11/26
 */
public class TestPersion {
    private String name;
    private String phone;

    public TestPersion(String name, String phone) {
        this.name = name;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
