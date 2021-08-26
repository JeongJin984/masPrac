package com.example.fooservice.db.entity;

import lombok.Getter;

@Getter
public class Account {
    private String username;
    private String password;

    public void setPassword(String password) {
        this.password = password;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
