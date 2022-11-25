package com.example.passwordmanager.db;

import android.provider.BaseColumns;

class PasswordTable {
    private PasswordTable(){}
    public static class PasswordEntry implements BaseColumns
    {
        public static final String TABLE_NAME="Password";
        public static final String USERNAME ="USERNAME";
        public static final String PASSWORD ="PASSWORD";
    }
}
