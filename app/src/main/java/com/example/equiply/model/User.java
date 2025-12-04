package com.example.equiply.model;


public class User {
    private String id;
    private String name;
    private String nim;

    private String email;


    public User() {}

    public User(String id,String name, String nim, String email) {
        this.id = id;
        this.name = name;
        this.nim = nim;
        this.email = email;
    }

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

    public String getNim() {
        return nim;
    }

    public void setNim(String nim) {
        this.nim = nim;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
