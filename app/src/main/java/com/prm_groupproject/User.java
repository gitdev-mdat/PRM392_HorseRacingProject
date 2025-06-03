package com.prm_groupproject;

public class User {
    String username;
    String password;
    int totalPoints;
    int winCount;
    int loseCount;

    public User(String username, String password, int totalPoints, int winCount, int loseCount) {
        this.username = username;
        this.password = password;
        this.totalPoints = totalPoints;
        this.winCount = winCount;
        this.loseCount = loseCount;
    }

    public User() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getTotalPoints() {
        return totalPoints;
    }

    public void setTotalPoints(int totalPoints) {
        this.totalPoints = totalPoints;
    }

    public int getWinCount() {
        return winCount;
    }

    public void setWinCount(int winCount) {
        this.winCount = winCount;
    }

    public int getLoseCount() {
        return loseCount;
    }

    public void setLoseCount(int loseCount) {
        this.loseCount = loseCount;
    }
}

