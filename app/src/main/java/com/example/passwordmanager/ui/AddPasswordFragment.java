package com.example.passwordmanager.ui;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.passwordmanager.R;
import com.example.passwordmanager.db.PasswordManagerDatabase;
import com.example.passwordmanager.model.Password;
import com.example.passwordmanager.model.Session;
import com.example.passwordmanager.ui.dialogue.ActionCompleteDialogue;
import com.example.passwordmanager.ui.dialogue.ErrorDialogue;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent;
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

public class AddPasswordFragment extends Fragment {
    MainActivity activity;
    ViewGroup viewGroup;

    public AddPasswordFragment(MainActivity activity, ViewGroup viewGroup) {
        this.activity = activity;
        this.viewGroup = viewGroup;
    }

    public AddPasswordFragment() {
    }

    public static AddPasswordFragment newInstance() {
        AddPasswordFragment fragment = new AddPasswordFragment();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (!Session.isSessionValid()) {
            ErrorDialogue errorDialogue = new ErrorDialogue(
                    activity,
                    viewGroup,
                    "Session Expired"
            );
            errorDialogue.showDialogue();
            activity.getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.fragment_container, new LogInFragment(
                            activity,
                            viewGroup
                    ), null).commit();
        }
    }

    ImageView appLogo;
    TextInputLayout usernameTextLayout, passwordTextLayout;
    TextInputEditText usernameText, passwordText;
    Button saveButton;
    TextView title;

    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_password, container, false);
        appLogo = view.findViewById(R.id.app_logo);

        usernameTextLayout = view.findViewById(R.id.username_text_layout);
        usernameText = view.findViewById(R.id.username_text);

        passwordTextLayout = view.findViewById(R.id.password_text_layout);
        passwordText = view.findViewById(R.id.password_text);

        saveButton = view.findViewById(R.id.save_button);
        title = view.findViewById(R.id.add_password_title);

        KeyboardVisibilityEvent.setEventListener(getActivity(), new KeyboardVisibilityEventListener() {
            AnimatorSet animatorSet = new AnimatorSet();
            float dy = 0;
            String property = "translationY";

            @Override
            public void onVisibilityChanged(boolean b) {
                if (b) {
                    dy = -330;
                } else {
                    dy = 0;
                }
                animatorSet.playTogether(
                        ObjectAnimator.ofFloat(appLogo, property, dy),
                        ObjectAnimator.ofFloat(usernameTextLayout, property, dy),
                        ObjectAnimator.ofFloat(passwordTextLayout, property, dy),
                        ObjectAnimator.ofFloat(saveButton, property, dy),
                        ObjectAnimator.ofFloat(title, property, dy)
                );
                animatorSet.setDuration(150);
                animatorSet.start();
            }
        });

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username = usernameText.getText().toString().trim();
                String password = passwordText.getText().toString().trim();
                boolean validation = true;
                if (username.length() == 0) {
                    usernameTextLayout.setError("Field required");
                    validation = false;
                }
                if (password.length() == 0) {
                    passwordTextLayout.setError("Field required");
                    validation = false;
                }

                if (validation) {
                    try {
                        Password passwordObj = new Password(getContext(), username, password);
                        PasswordManagerDatabase passwordManagerDatabase
                                = new PasswordManagerDatabase(getContext());
                        passwordManagerDatabase.savePassword(passwordObj);
                        ActionCompleteDialogue actionCompleteDialogue =
                                new ActionCompleteDialogue(
                                        getContext(),
                                        activity.fragmentContainer,
                                        "Password saved successfully",
                                        2000
                                );
                        actionCompleteDialogue.showDialogue();
                    } catch (Exception e) {
                        ErrorDialogue errorDialogue = new ErrorDialogue(
                                getContext(), activity.fragmentContainer,
                                e.getMessage()
                        );
                        errorDialogue.showDialogue();
                    }
                }

            }
        });
        return view;
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        if (!Session.isExpired())
            Session.saveSession();
    }

    @Override
    public void onDetach () {
        super.onDetach();
        if (!Session.isExpired())
            Session.saveSession();
    }

    @Override
    public void onStop () {
        super.onStop();
        if (!Session.isExpired())
            Session.saveSession();

    }
}