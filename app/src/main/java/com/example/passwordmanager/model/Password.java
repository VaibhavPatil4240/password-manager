package com.example.passwordmanager.model;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.passwordmanager.db.PasswordManagerDatabase;
import com.example.passwordmanager.service.encryption.EncryptDecrypt;
import com.example.passwordmanager.service.encryption.SecretKeyHandler;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class Password {

    private String username,password;
    long id;
    private SecretKey secretKey;
    private EncryptDecrypt encryptDecrypt;
    Context context;
    private boolean passwordUnlocked=false;

    public Password(Context context,@NonNull String username,@NonNull String password)
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        if(username == null || password == null)
            throw new NullPointerException("Username and Password can't be null");
        this.username=username;
        secretKey= SecretKeyHandler.getKey(context);
        encryptDecrypt=new EncryptDecrypt(secretKey);
        this.password=encryptDecrypt.encryptMessage(password);
        this.context=context;
    }

    public Password(Context context,@NonNull String username,long id)
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException, InvalidKeyException {
        if(username == null)
            throw new NullPointerException("Username can't be null");
        this.username=username;
        this.password=null;
        this.id=id;
        secretKey= SecretKeyHandler.getKey(context);
        encryptDecrypt=new EncryptDecrypt(secretKey);
        this.context=context;
    }

    public Password(Context context,@NonNull long id,@NonNull String username,@NonNull String password)
            throws IOException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException,
            BadPaddingException, InvalidKeyException
    {
        if(username == null || password == null)
            throw new NullPointerException("Username and Password can't be null");
        this.id=id;
        this.username=username;
        secretKey= SecretKeyHandler.getKey(context);
        encryptDecrypt=new EncryptDecrypt(secretKey);
        this.password=encryptDecrypt.encryptMessage(password);
        this.context=context;
    }

    public long getId() { return this.id; }

    public void setId(long id) { this.id=id; }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    //Secure this method with some authentication
    //Ex-use lock screen unlock method for authentication

    public String getDecryptedPassword()
            throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException
    {
        PasswordManagerDatabase passwordManagerDatabase=new PasswordManagerDatabase(context);
        this.password = encryptDecrypt.decryptMessage(passwordManagerDatabase.getPassword(this.id));
        return password;
    }

    public void setPassword(String password)
            throws NoSuchPaddingException, IllegalBlockSizeException, NoSuchAlgorithmException,
            BadPaddingException, InvalidKeyException
    {

        this.password = encryptDecrypt.encryptMessage(password);
    }
    public void setPasswordUnlocked(boolean passwordUnlocked)
    {
        this.passwordUnlocked=passwordUnlocked;
    }
    public boolean getPasswordUnlocked()
    {
        return this.passwordUnlocked;
    }
    @Override
    public String toString() {
        return "Password{" +
                "ID '"+ id + "\'"+
                "username='" + username + '\'' +
                ", password='" + password+ '\'' +
                '}';
    }
}
