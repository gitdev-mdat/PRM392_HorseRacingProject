package com.prm_groupproject;

public class User {
    public String username;
    public String password;
    public int points;

    // Constructor
    public User(String username, String password) {
        this.username = username;
        this.password = password;
        this.points = 100; // mặc định khi tạo user mới
    }

    // Constructor đầy đủ
    public User(String username, String password, int points) {
        this.username = username;
        this.password = password;
        this.points = points;
    }
}
