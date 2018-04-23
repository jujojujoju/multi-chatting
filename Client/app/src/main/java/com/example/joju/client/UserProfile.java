package com.example.joju.client;

public class UserProfile {
    private String name;
    private String status;
    private String id;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public UserProfile(String id, String name, String status) {
        setId(id);
        setName(name);
        setStatus(status);
    }
    public UserProfile(String name) {
        setId("AllUser");
        setName(name);
    }
}
