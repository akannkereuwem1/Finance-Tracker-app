package com.example.financeapp;

public class Session {
    private static int userId;
    private static String username;

    public static void setUser(int id, String user) {
        userId = id;
        username = user;
    }

    public static int getUserId() {
        return userId;
    }
    private Session() {}

    public static String getUsername() {
        return username;
    }

    public static void clear() {
        userId = 0;
        username = null;
    }
}
