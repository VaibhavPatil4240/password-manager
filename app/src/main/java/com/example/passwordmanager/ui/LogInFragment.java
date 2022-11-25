package com.example.passwordmanager.ui;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;

import com.example.passwordmanager.R;
import com.example.passwordmanager.model.Session;
import com.example.passwordmanager.service.otp.Authenticator;
import com.example.passwordmanager.ui.dialogue.ErrorDialogue;
import com.google.android.material.textfield.TextInputEditText;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LogInFragment extends Fragment {
    MainActivity activity;
    ViewGroup container;
    public LogInFragment()
    {
        this.activity=(MainActivity) getActivity();
    }
    public LogInFragment(MainActivity activity,ViewGroup container) {
        this.container = container;
        this.activity=activity;
    }

    public static LogInFragment newInstance(String param1, String param2) {
        LogInFragment fragment = new LogInFragment();
        return fragment;
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity.bottomNavigationView.setVisibility(View.GONE);
    }
    TextInputEditText phoneText;
    Button getOtpButton;
    ProgressBar otpProgressBar;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.fragment_log_in, container, false);
        getOtpButton=view.findViewById(R.id.otp_button);
        phoneText=view.findViewById(R.id.phoneText);
        otpProgressBar=view.findViewById(R.id.otp_progress);
        activity.bottomNavigationView.setVisibility(View.GONE);
        if(Session.phone==null)
        {
            getOtpButton.setText("Register");
        }

        getOtpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String phone=phoneText.getText().toString();

                if(validatePhoneNumber(phone))
                {
                    if(Session.phone==null || phone.equals(Session.phone.substring(3)))
                    {
                        otpProgressBar.setVisibility(View.VISIBLE);
                        getOtpButton.setClickable(false);
                        Authenticator authenticator=new Authenticator(activity,container,phone);
                        authenticator.sendOtp(otpProgressBar,getOtpButton);
                    }
                    else
                    {
                        ErrorDialogue errorDialogue=new ErrorDialogue(
                                activity,
                                container,
                                "Enter registered phone number"
                        );
                        errorDialogue.showDialogue();
                    }
                }
                else
                {
                    ErrorDialogue errorDialogue=new ErrorDialogue(
                            activity,
                            container,
                            "Please enter valid phone number"
                    );
                    errorDialogue.showDialogue();
                }
            }
        });
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        activity.bottomNavigationView.setVisibility(View.VISIBLE);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        activity.bottomNavigationView.setVisibility(View.VISIBLE);
    }

    private boolean validatePhoneNumber(String phoneNumber)
    {
        Pattern pattern = Pattern.compile("(0/91)?[7-9][0-9]{9}");
        Matcher matcher =pattern.matcher(phoneNumber);

        return matcher.find() && matcher.group().equals(phoneNumber);
    }
}