package com.example.passwordmanager.ui;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.KEYGUARD_SERVICE;

import android.annotation.SuppressLint;
import android.app.KeyguardManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.passwordmanager.R;
import com.example.passwordmanager.adapter.PasswordCardAdapter;
import com.example.passwordmanager.db.PasswordManagerDatabase;
import com.example.passwordmanager.model.Password;
import com.example.passwordmanager.model.Session;
import com.example.passwordmanager.ui.dialogue.ErrorDialogue;
import com.example.passwordmanager.viewholder.PasswordCardViewHolder;
import com.google.android.material.snackbar.Snackbar;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class PasswordListFragment  extends Fragment{

    MainActivity activity;
    ViewGroup viewGroup;
    PasswordCardAdapter passwordCardAdapter;
    private RecyclerView recyclerView;
    private static int CODE_AUTHENTICATION_VERIFICATION=241;
    Password password=null;
    boolean copy=false;
    public PasswordListFragment(MainActivity activity, ViewGroup viewGroup) {
        this.activity = activity;
        this.viewGroup = viewGroup;
    }

    public PasswordListFragment() {
        // Required empty public constructor
    }

    public static PasswordListFragment newInstance() {
        PasswordListFragment fragment = new PasswordListFragment();
        return fragment;
    }

    ActivityResultLauncher<Intent> activityResultLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()==RESULT_OK)
                    {
                        Toast.makeText(getContext(),"Verified",Toast.LENGTH_LONG).show();
                        try {
                            String sPassword=password.getDecryptedPassword();
                            if(copy)
                            {
                                passwordCardAdapter.copyToClipboard("Password",sPassword);
                            }
                            else password.setPasswordUnlocked(true);

                            passwordCardAdapter.notifyDataSetChanged();

                        }catch (Exception e)
                        {
                            ErrorDialogue errorDialogue =new ErrorDialogue(getContext(),viewGroup,e.getMessage());
                            errorDialogue.showDialogue();
                        }
                    }
                    else
                    {
                        Toast.makeText(getContext(),"Need Verification",Toast.LENGTH_LONG).show();
                    }
                }
            });

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(!Session.isSessionValid())
        {
            ErrorDialogue errorDialogue=new ErrorDialogue(
                    activity,
                    viewGroup,
                    "Session Expired"
            );
            errorDialogue.showDialogue();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,new LogInFragment(
                            activity,
                            viewGroup
                    ),null).commit();
        }
    }
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_password_list, container, false);
        recyclerView=view.findViewById(R.id.password_list_recyclerview);
        PasswordManagerDatabase passwordManagerDatabase=new PasswordManagerDatabase(activity.getApplicationContext());
        ArrayList<Password> passwords=new ArrayList<>();
        try {
            passwords=passwordManagerDatabase.getUsernames();
        } catch (Exception e) {
            ErrorDialogue errorDialogue=new ErrorDialogue(
                    activity,
                    viewGroup,
                    e.getMessage()
            );
            errorDialogue.showDialogue();
        }
        passwordCardAdapter=new PasswordCardAdapter(activity,getActivity(),getContext(),PasswordListFragment.this,passwords);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(getContext());
        recyclerView.setLayoutManager(linearLayoutManager);
        recyclerView.setAdapter(passwordCardAdapter);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 0 && activity.bottomNavigationView.isShown()) {
                    activity.bottomNavigationView.setVisibility(View.GONE);
                } else if (dy < 0 ) {
                    activity.bottomNavigationView.setVisibility(View.VISIBLE);

                }
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!Session.isExpired())
            Session.saveSession();
    }

    public void launchLock(Password password,boolean copy)
    {
        KeyguardManager km = (KeyguardManager)getContext().getSystemService(KEYGUARD_SERVICE);
        Intent i = km.createConfirmDeviceCredentialIntent("Authentication Required", "Show password");
        this.password=password;
        this.copy=copy;
        startActivity(i);
        activityResultLauncher.launch(i);

    }
    @Override
    public void onResume(){
        super.onResume();
        if(!Session.isSessionValid())
        {
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container,new LogInFragment(
                            activity,
                            viewGroup
                    ),null).commit();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        if(!Session.isExpired())
            Session.saveSession();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(!Session.isExpired())
            Session.saveSession();
    }
}