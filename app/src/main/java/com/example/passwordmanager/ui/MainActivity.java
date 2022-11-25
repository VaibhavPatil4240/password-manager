package com.example.passwordmanager.ui;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.passwordmanager.R;
import com.example.passwordmanager.db.PasswordManagerDatabase;
import com.example.passwordmanager.model.Password;
import com.example.passwordmanager.model.Session;
import com.example.passwordmanager.model.UserName;
import com.example.passwordmanager.ui.dialogue.ActionCompleteDialogue;
import com.example.passwordmanager.ui.dialogue.ErrorDialogue;
import com.example.passwordmanager.ui.dialogue.MessageDialogue;
import com.google.android.material.bottomappbar.BottomAppBar;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

import org.joda.time.DateTime;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity{
    FrameLayout fragmentContainer;
    BottomNavigationView bottomNavigationView;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_main);
        Session.configSession(MainActivity.this);
        bottomNavigationView=findViewById(R.id.bottom_navbar);
        fragmentContainer=findViewById(R.id.fragment_container);
        try {
            if(Session.createSession())
            {
                getSupportFragmentManager().beginTransaction()
                        .add(R.id.fragment_container,new PasswordListFragment(
                                MainActivity.this,
                                fragmentContainer
                        ),null)
                        .commit();
            }
            else
            {
                if(Session.lastLogout==null)
                {
                    MessageDialogue messageDialogue=new MessageDialogue(
                            MainActivity.this,
                            fragmentContainer,
                            getResources().getString(R.string.information)
                    );
                    messageDialogue.showDialogue();

                }
                else
                {
                    ErrorDialogue errorDialogue=new ErrorDialogue(
                            MainActivity.this,
                            fragmentContainer,
                            "Session Expired"
                    );
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new LogInFragment(
                        MainActivity.this
                        ,fragmentContainer
                )).commit();
            }
        } catch (Exception e) {
            ErrorDialogue errorDialogue=new ErrorDialogue(
                    MainActivity.this,
                    fragmentContainer,
                    e.getMessage()
            );
            errorDialogue.showDialogue();
        }

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Fragment currentFragment=getSupportFragmentManager().findFragmentById(R.id.fragment_container);

                if(item.getItemId() == R.id.home_button)
                {
                    if(!(currentFragment instanceof PasswordListFragment))
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new PasswordListFragment(
                                MainActivity.this,
                                fragmentContainer
                        ),null).commit();
                    return true;
                }
                else if(item.getItemId() == R.id.add_button)
                {
                    if(!(currentFragment instanceof AddPasswordFragment))
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new AddPasswordFragment(
                                MainActivity.this,
                                fragmentContainer
                        ),null).commit();
                    return true;
                }
                else if(item.getItemId() == R.id.settings_button)
                {
                    if(!(currentFragment instanceof SettingsFragment))
                        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new SettingsFragment(
                                MainActivity.this,
                                fragmentContainer
                        ),null).commit();
                    return true;
                }
                return false;
            }
        });
    }
}