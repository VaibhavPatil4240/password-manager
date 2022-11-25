package com.example.passwordmanager.model;

public class UserName {
    public String username ;
    public long id;

    public UserName(String username,long id)
    {
        this.id=id;
        this.username=username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }
}
