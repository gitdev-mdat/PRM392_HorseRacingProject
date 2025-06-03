package com.prm_groupproject;

import android.content.Context;
import android.content.SharedPreferences;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class UserManager {
    private static final String PREFS_NAME = "HorseRacingPrefs";
    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_TOTAL_POINTS = "total_points";
    private static final String KEY_GAMES_PLAYED = "games_played";
    private static final String KEY_GAMES_WON = "games_won";
    private static final String KEY_IS_LOGGED_IN = "is_logged_in";
    
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    
    public UserManager(Context context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }
    
    // Hash password để bảo mật
    private String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(password.getBytes());
            StringBuilder hexString = new StringBuilder();
            
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            return password; // Fallback nếu không hash được
        }
    }
    
    // Đăng ký tài khoản mới
    public boolean registerUser(String username, String password) {
        if (username == null || username.trim().isEmpty() || 
            password == null || password.trim().isEmpty()) {
            return false;
        }
        
        // Kiểm tra username đã tồn tại chưa
        if (prefs.contains(KEY_USERNAME)) {
            return false; // Đã có tài khoản
        }
        
        // Lưu thông tin user
        editor.putString(KEY_USERNAME, username.trim());
        editor.putString(KEY_PASSWORD, hashPassword(password));
        editor.putInt(KEY_TOTAL_POINTS, 1000); // Điểm khởi đầu
        editor.putInt(KEY_GAMES_PLAYED, 0);
        editor.putInt(KEY_GAMES_WON, 0);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        
        return editor.commit();
    }
    
    // Đăng nhập
    public boolean loginUser(String username, String password) {
        if (username == null || password == null) return false;
        
        String savedUsername = prefs.getString(KEY_USERNAME, "");
        String savedPassword = prefs.getString(KEY_PASSWORD, "");
        
        if (savedUsername.equals(username.trim()) && 
            savedPassword.equals(hashPassword(password))) {
            editor.putBoolean(KEY_IS_LOGGED_IN, true);
            editor.apply();
            return true;
        }
        
        return false;
    }
    
    // Đăng xuất
    public void logoutUser() {
        editor.putBoolean(KEY_IS_LOGGED_IN, false);
        editor.apply();
    }
    
    // Kiểm tra đã đăng nhập chưa
    public boolean isLoggedIn() {
        return prefs.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    
    // Kiểm tra đã có tài khoản chưa
    public boolean hasAccount() {
        return prefs.contains(KEY_USERNAME);
    }
    
    // Lấy thông tin user
    public String getUsername() {
        return prefs.getString(KEY_USERNAME, "");
    }
    
    public int getTotalPoints() {
        return prefs.getInt(KEY_TOTAL_POINTS, 1000);
    }
    
    public int getGamesPlayed() {
        return prefs.getInt(KEY_GAMES_PLAYED, 0);
    }
    
    public int getGamesWon() {
        return prefs.getInt(KEY_GAMES_WON, 0);
    }
    
    // Cập nhật điểm số
    public void updatePoints(int newPoints) {
        editor.putInt(KEY_TOTAL_POINTS, newPoints);
        editor.apply();
    }
    
    // Cập nhật thống kê game
    public void updateGameStats(boolean won) {
        int gamesPlayed = getGamesPlayed() + 1;
        int gamesWon = getGamesWon() + (won ? 1 : 0);
        
        editor.putInt(KEY_GAMES_PLAYED, gamesPlayed);
        editor.putInt(KEY_GAMES_WON, gamesWon);
        editor.apply();
    }
    
    // Validate input
    public static boolean isValidUsername(String username) {
        return username != null && username.trim().length() >= 3 && 
               username.trim().length() <= 20 && username.matches("[a-zA-Z0-9_]+");
    }
    
    public static boolean isValidPassword(String password) {
        return password != null && password.length() >= 4;
    }
    
    // Reset tài khoản (để test)
    public void resetAccount() {
        editor.clear();
        editor.apply();
    }
}
