package com.example.passwordmanager.service.otp;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.passwordmanager.R;
import com.example.passwordmanager.model.Session;
import com.example.passwordmanager.ui.MainActivity;
import com.example.passwordmanager.ui.PasswordListFragment;
import com.example.passwordmanager.ui.dialogue.ActionCompleteDialogue;
import com.example.passwordmanager.ui.dialogue.ErrorDialogue;
import com.example.passwordmanager.ui.dialogue.MessageDialogue;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import java.util.concurrent.TimeUnit;

public class Authenticator {
    private String verificationId;
    private String phoneNumber;
    private MainActivity activity;
    private FirebaseAuth mAuth;
    ViewGroup viewGroup;

    public Authenticator( MainActivity activity, ViewGroup viewGroup, String phoneNumber)
    {
        this.phoneNumber="+91"+phoneNumber;
        mAuth = FirebaseAuth.getInstance();
        this.activity=activity;
        this.viewGroup=viewGroup;
    }
    public void sendOtp(ProgressBar progressBar, Button button)
    {
        PhoneAuthProvider.getInstance().verifyPhoneNumber(this.phoneNumber, 60, TimeUnit.SECONDS, activity, new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                verificationId=s;
                ActionCompleteDialogue actionCompleteDialogue=new ActionCompleteDialogue(
                        activity,
                        viewGroup,
                        "OTP Sent",
                        3000
                );
                actionCompleteDialogue.showDialogue();
            }

            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredential(phoneAuthCredential,progressBar,button);
            }
            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                ErrorDialogue errorDialogue=new ErrorDialogue(
                        activity,
                        viewGroup,
                        e.getMessage()
                );
                errorDialogue.showDialogue();
                button.setClickable(true);
                progressBar.setVisibility(View.INVISIBLE);
            }
        });
    }

   private void signInWithPhoneAuthCredential(PhoneAuthCredential credential,ProgressBar progressBar,Button button)
   {
        mAuth.signInWithCredential(credential).addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                button.setClickable(true);
                if(task.isSuccessful())
                {
                    Session.phone=phoneNumber;
                    Session.saveSession();
                    ActionCompleteDialogue actionCompleteDialogue=new ActionCompleteDialogue(
                            activity,
                            viewGroup,
                            "Verification Complete",
                            4000
                    );
                    actionCompleteDialogue.showDialogue();
                    progressBar.setVisibility(View.INVISIBLE);
                    activity.getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container,new PasswordListFragment(
                                    activity,
                                    viewGroup
                            ),null).commit();
                }
                else
                {
                    MessageDialogue messageDialogue=new MessageDialogue(
                            activity,
                            viewGroup,
                            task.getResult().toString()
                    );
                    messageDialogue.showDialogue();
                    progressBar.setVisibility(View.INVISIBLE);
                }
            }
        });
   }
}
