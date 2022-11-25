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
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.Toast;

import com.example.passwordmanager.R;
import com.example.passwordmanager.model.Session;
import com.example.passwordmanager.ui.dialogue.ActionCompleteDialogue;
import com.example.passwordmanager.ui.dialogue.ErrorDialogue;
import com.example.passwordmanager.ui.dialogue.SessionTimeoutDialogue;
import com.google.android.material.slider.Slider;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SettingsFragment extends Fragment {
    Slider lockTimeSlider;
    TextInputEditText registeredPhoneText;
    TextInputLayout registeredPhoneTextLayout;
    CardView editRegisteredPhoneButton;
    Button saveChangesButton,resetSettingsButton,lockSessionButton;
    boolean isSettingsChanged=false;
    int lockTime=0;
    ViewGroup viewGroup;
    MainActivity activity;

    ActivityResultLauncher<Intent> activityResultLauncher=registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode()==RESULT_OK)
                    {
                        Snackbar.make(saveChangesButton,"Verified",Snackbar.LENGTH_SHORT).show();
                        isSettingsChanged=true;
                        saveChangesButton.setEnabled(true);
                        registeredPhoneText.setEnabled(true);
                        registeredPhoneTextLayout.setEndIconActivated(true);

                    }
                    else
                    {
                        Snackbar.make(saveChangesButton,"Need Verification",Snackbar.LENGTH_SHORT).show();
                    }
                }
            });

    public SettingsFragment(MainActivity activity,ViewGroup viewGroup)
    {
        this.activity=activity;
        this.viewGroup=viewGroup;
    }
    public SettingsFragment() {
        // Required empty public constructor
    }
    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        return fragment;
    }

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
        View view= inflater.inflate(R.layout.fragment_settings, container, false);
        lockTimeSlider=view.findViewById(R.id.lock_slider);
        registeredPhoneText=view.findViewById(R.id.registered_phone_text);
        registeredPhoneTextLayout=view.findViewById(R.id.registered_phone_text_layout);
        editRegisteredPhoneButton=view.findViewById(R.id.edit_registered_number_button);
        saveChangesButton=view.findViewById(R.id.save_settings_button);
        resetSettingsButton=view.findViewById(R.id.reset_button);
        lockSessionButton=view.findViewById(R.id.lock_session_button);

        registeredPhoneText.setText(Session.phone.substring(3));
        lockTime=Session.lockTime;
        lockTimeSlider.setValue(Session.lockTime);

        lockTimeSlider.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                isSettingsChanged=true;
                lockTime=(int)value;
                resetSettingsButton.setEnabled(true);
                saveChangesButton.setEnabled(true);
            }
        });

        resetSettingsButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                lockTimeSlider.setValue(Session.lockTime);
                registeredPhoneText.setText(Session.phone.substring(3));
                registeredPhoneText.setSelection(10);
            }
        });

        saveChangesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Session.lockTime=lockTime;
                String newPhoneNumber=registeredPhoneText.getText().toString();
                if(newPhoneNumber.length()==10)
                {
                    if(validatePhoneNumber(newPhoneNumber))
                    {
                        Session.phone="+91"+newPhoneNumber;
                        Session.saveSession();
                        ActionCompleteDialogue actionCompleteDialogue
                                =new ActionCompleteDialogue(getContext(),viewGroup,"Changes Saved",3000);
                        actionCompleteDialogue.showDialogue();
                        saveChangesButton.setEnabled(false);
                        resetSettingsButton.setEnabled(false);
                        registeredPhoneText.setEnabled(false);
                    }
                    else
                    {
                        registeredPhoneTextLayout.setError("Enter a valid phone number");
                    }
                }
                else
                {
                    registeredPhoneTextLayout.setError("Enter a valid phone number");
                }
            }
        });

        editRegisteredPhoneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                KeyguardManager km = (KeyguardManager)getContext().getSystemService(KEYGUARD_SERVICE);
                Intent i = km.createConfirmDeviceCredentialIntent("Authentication Required", "Show password");
                startActivity(i);
                activityResultLauncher.launch(i);
            }
        });

        registeredPhoneText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            @Override
            public void afterTextChanged(Editable editable) {
                resetSettingsButton.setEnabled(true);
                saveChangesButton.setEnabled(true);
                isSettingsChanged=true;
                registeredPhoneTextLayout.setError(null);
            }
        });
        registeredPhoneTextLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getContext(),"Clicked",Toast.LENGTH_LONG).show();
                registeredPhoneText.getText().clear();
            }
        });

        lockSessionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SessionTimeoutDialogue sessionTimeoutDialogue=
                        new SessionTimeoutDialogue(
                                activity,
                                getContext(),
                                viewGroup,
                                10000
                        );
                sessionTimeoutDialogue.showDialogue();

            }
        });
        return view;
    }

    private boolean validatePhoneNumber(String phoneNumber)
    {
        Pattern pattern = Pattern.compile("(0/91)?[7-9][0-9]{9}");
        Matcher matcher =pattern.matcher(phoneNumber);
        return matcher.find() && matcher.group().equals(phoneNumber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(!Session.isExpired())
            Session.saveSession();
    }

    @Override
    public void onDetach() {
        super.onDetach();
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