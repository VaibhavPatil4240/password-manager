package com.example.passwordmanager.model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;


public class Session {
    static public String phone;
    static public DateTime lastLogout;
    static public int lockTime;
    static public Context context;
    static private SharedPreferences sharedPreferences;
    private static boolean expired;

    static public void configSession(Context context)
    {
        Session.context=context;
        lockTime=10;
        DateTimeZone.setDefault(DateTimeZone.UTC);
    }

    static public boolean createSession() throws Exception {
        if(context==null) throw new NullPointerException("Session->context null");
        sharedPreferences=context.getSharedPreferences("session",Context.MODE_PRIVATE);
        String sPhone,sLastLogout,sLockTime;
        sPhone=sharedPreferences.getString("phone",null);
        sLastLogout=sharedPreferences.getString("last-logout",null);
        sLockTime=sharedPreferences.getString("lock-time",null);
        if(sPhone==null) return false;
        phone=sPhone;
        lastLogout=new DateTime(sLastLogout);
        lockTime=Integer.valueOf(sLockTime);
        return isSessionValid();
    }

    static public void saveSession()
    {
        sharedPreferences=context.getSharedPreferences("session",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        Session.lastLogout=new DateTime();
        editor.putString("phone",Session.phone);
        editor.putString("last-logout",Session.lastLogout.toString());
        editor.putString("lock-time",String.valueOf(Session.lockTime));
        Session.expired=false;
        editor.commit();
    }
    public static boolean isSessionValid()
    {
        Session.expired=true;
        if(lastLogout==null) return false;

        DateTime dateTime=lastLogout.plusMinutes(lockTime);

        DateTime currentDateTime=new DateTime();

        if(currentDateTime.isBefore(dateTime))
        {
            Session.expired=false;
            return true;
        }
        return false;
    }

    public static void endSession()
    {
        lastLogout=lastLogout.minusMinutes(Session.lockTime);
        sharedPreferences=context.getSharedPreferences("session",Context.MODE_PRIVATE);
        SharedPreferences.Editor editor= sharedPreferences.edit();
        editor.putString("phone",Session.phone);
        editor.putString("last-logout",Session.lastLogout.toString());
        editor.putString("lock-time",String.valueOf(Session.lockTime));
        editor.commit();
        Session.expired=true;
    }

    public static boolean isExpired()
    {
        return Session.expired;
    }

}
