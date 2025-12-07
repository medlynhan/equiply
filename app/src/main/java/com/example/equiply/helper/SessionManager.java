package com.example.equiply.helper;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private static final String PREF_NAME = "UserSession";
    private static final String KEY_USER_ID = "USER_ID";
    private static final String KEY_ROLE = "USER_ROLE";
    private static final String KEY_NAME = "USER_NAME";
    private static final String KEY_EMAIL = "USER_EMAIL";

    private final SharedPreferences prefs;
    private final SharedPreferences.Editor editor;

    public SessionManager(Context context) {
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveUserSession(String userId, String role, String name, String email) {
        editor.putString(KEY_USER_ID, userId);
        editor.putString(KEY_ROLE, role);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_EMAIL, email);
        editor.apply();
    }

    public String getRole() {
        return prefs.getString(KEY_ROLE, "student");
    }

    public String getUserId() {
        return prefs.getString(KEY_USER_ID, null);
    }

    public String getName() {
        return prefs.getString(KEY_NAME, "Student");
    }

    public String getEmail() {
        return prefs.getString(KEY_EMAIL, "");
    }

    public void logout() {
        editor.clear();
        editor.commit();
    }

    public boolean isAdmin() {
        return "admin".equals(getRole());
    }
}