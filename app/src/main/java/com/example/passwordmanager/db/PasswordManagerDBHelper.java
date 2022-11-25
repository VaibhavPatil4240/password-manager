package com.example.passwordmanager.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

class PasswordManagerDBHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "PasswordManager.db";

    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + PasswordTable.PasswordEntry.TABLE_NAME + " (" +
                    PasswordTable.PasswordEntry._ID + " INTEGER PRIMARY KEY," +
                    PasswordTable.PasswordEntry.PASSWORD+ " TEXT," +
                    PasswordTable.PasswordEntry.USERNAME + " TEXT)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + PasswordTable.PasswordEntry.TABLE_NAME;

    public PasswordManagerDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase){
        sqLiteDatabase.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL(SQL_DELETE_ENTRIES);
        onCreate(sqLiteDatabase);
    }
}
