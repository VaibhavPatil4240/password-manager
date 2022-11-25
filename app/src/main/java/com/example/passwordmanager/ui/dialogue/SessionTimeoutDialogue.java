package com.example.passwordmanager.ui.dialogue;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.passwordmanager.R;
import com.example.passwordmanager.model.Session;
import com.example.passwordmanager.ui.LogInFragment;
import com.example.passwordmanager.ui.MainActivity;

public class SessionTimeoutDialogue {

    Context context;
    ViewGroup viewGroup;
    TextView messageText;
    Button okButton;
    int dismissTime;
    MainActivity activity;
    CountDownTimer countDownTimer;
    public SessionTimeoutDialogue(MainActivity activity, Context context, ViewGroup viewGroup, int dismissTime){
        this.activity=activity;
        this.context=context;
        this.viewGroup=viewGroup;
        this.dismissTime=dismissTime;
    }

    @SuppressLint("MissingInflatedId")
    public void showDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.message_dialogue, viewGroup, false);
        messageText = view.findViewById(R.id.message_text);
        okButton= view.findViewById(R.id.ok_button);
        okButton.setText("Cancel");
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        countDownTimer=new CountDownTimer(dismissTime, 1000) {
            int counter=dismissTime/1000;
            @Override
            public void onTick(long l) {
                messageText.setText("You will be logged out after "+l/1000+" sec.");
            }
            @Override
            public void onFinish() {
                Session.endSession();
                alertDialog.dismiss();
                activity.getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new LogInFragment(
                        activity,
                        viewGroup
                )).commit();
            }
        };

        countDownTimer.start();

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
                countDownTimer.cancel();
            }
        });


    }
}
