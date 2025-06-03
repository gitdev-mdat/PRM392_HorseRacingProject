package com.prm_groupproject;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

public class UserManager {
    private static final String PREFS_NAME = "user_prefs";
    private static final String USERS_KEY = "users";
    private static final String LOGGED_IN_KEY = "logged_in_user";

    private SharedPreferences prefs;
    private Gson gson;

    public UserManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        gson = new Gson();
    }

    public List<User> getAllUsers() {
        String json = prefs.getString(USERS_KEY, "[]");
        Type type = new TypeToken<List<User>>() {}.getType();
        List<User> users = gson.fromJson(json, type);
        return users != null ? users : new ArrayList<>();
    }

    public void saveAllUsers(List<User> users) {
        String json = gson.toJson(users);
        prefs.edit().putString(USERS_KEY, json).apply();
    }

    public boolean registerUser(String username, String password) {
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.username.equals(username)) return false; // tài khoản tồn tại
        }
        String hashedPassword = hashPassword(password);
        users.add(new User(username, hashedPassword, 1000, 0, 0));
        saveAllUsers(users);
        return true;
    }

    public boolean loginUser(String username, String password) {
        String hashedPassword = hashPassword(password);
        for (User u : getAllUsers()) {
            if (u.username.equals(username) && u.password.equals(hashedPassword)) {
                prefs.edit().putString(LOGGED_IN_KEY, username).apply();
                return true;
            }
        }
        return false;
    }

    public boolean hasAccount() {
        List<User> users = getAllUsers();
        return users != null && !users.isEmpty();
    }

    public boolean isLoggedIn() {
        return prefs.contains(LOGGED_IN_KEY);
    }

    public String getLoggedInUsername() {
        return prefs.getString(LOGGED_IN_KEY, null);
    }

    public User getCurrentUser() {
        String username = getLoggedInUsername();
        if (username == null) return null;
        for (User u : getAllUsers()) {
            if (u.username.equals(username)) return u;
        }
        return null;
    }

    public void updatePoints(int points) {
        User current = getCurrentUser();
        if (current == null) return;
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.username.equals(current.username)) {
                u.totalPoints = points;
                break;
            }
        }
        saveAllUsers(users);
    }

    public void updateGameStats(boolean win) {
        User current = getCurrentUser();
        if (current == null) return;
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.username.equals(current.username)) {
                if (win) u.winCount++;
                else u.loseCount++;
                break;
            }
        }
        saveAllUsers(users);
    }

    public void logout() {
        prefs.edit().remove(LOGGED_IN_KEY).apply();
    }

    public static boolean isValidUsername(String username) {
        return username != null && username.matches("^[a-zA-Z0-9_]{3,20}$");
    }

    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 4;
    }

    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] digest = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : digest) sb.append(String.format("%02x", b));
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return password; // fallback (rủi ro)
        }
    }
}
