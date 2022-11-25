package com.example.passwordmanager.db;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.example.passwordmanager.model.Password;
import com.example.passwordmanager.model.UserName;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class PasswordManagerDatabase {
    private static Context context;
    private  PasswordManagerDBHelper passwordManagerDBHelper;
    private  SQLiteDatabase writableDb,readableDb;

    public PasswordManagerDatabase(Context context)
    {
        PasswordManagerDatabase.context=context;
        passwordManagerDBHelper=new PasswordManagerDBHelper(context);
        writableDb=passwordManagerDBHelper.getWritableDatabase();
        readableDb=passwordManagerDBHelper.getReadableDatabase();
    }

    public  long savePassword(Password password)
    {
        long rowId;
        ContentValues contentValues=new ContentValues();
        contentValues.put(PasswordTable.PasswordEntry.USERNAME,password.getUsername());
        contentValues.put(PasswordTable.PasswordEntry.PASSWORD,password.getPassword());

        rowId=writableDb.insert(PasswordTable.PasswordEntry.TABLE_NAME,null,contentValues);

        return rowId;
    }
    @SuppressLint("Range")
    public String getPassword(long id)
    {
        String query=
                "SELECT " + PasswordTable.PasswordEntry.PASSWORD +
                        " FROM " + PasswordTable.PasswordEntry.TABLE_NAME +
                        " WHERE " + PasswordTable.PasswordEntry._ID + " = '"+ id+"'";
        Cursor cursor= readableDb.rawQuery(query,null);
        cursor.moveToFirst();
        String password=cursor.getString(cursor.getColumnIndex(PasswordTable.PasswordEntry.PASSWORD));
        cursor.close();
        return password;
    }

    @SuppressLint("Range")
    public  ArrayList<Password> getUsernames() throws NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        ArrayList<Password> passwords=new ArrayList<>();

        String query=
                "SELECT " + PasswordTable.PasswordEntry.USERNAME
                        +", "+ PasswordTable.PasswordEntry._ID
                        +", " + PasswordTable.PasswordEntry.PASSWORD
                        +" FROM " +PasswordTable.PasswordEntry.TABLE_NAME;

        Cursor cursor=readableDb.rawQuery(query,null);
        if(cursor.moveToFirst())
        {
            do
            {
                String username;
                long id;
                id= cursor.getLong(cursor.getColumnIndex(PasswordTable.PasswordEntry._ID));
                username= cursor.getString(cursor.getColumnIndex(PasswordTable.PasswordEntry.USERNAME));
                Password password=new Password(context,username,id);
                passwords.add(password);
            }while (cursor.moveToNext());
        }
        cursor.close();
        return passwords;
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        writableDb.close();
        readableDb.close();
    }
}
