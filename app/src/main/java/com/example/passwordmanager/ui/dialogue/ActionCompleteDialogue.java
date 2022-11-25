package com.example.passwordmanager.ui.dialogue;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.passwordmanager.R;

public class ActionCompleteDialogue {
    String message;
    Context context;
    ViewGroup viewGroup;
    TextView messageText;
    int dismissTime;
    public ActionCompleteDialogue(Context context,ViewGroup viewGroup,String message,int dismissTime) {
        this.message = message;
        this.context=context;
        this.viewGroup=viewGroup;
        this.dismissTime=dismissTime;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SuppressLint("MissingInflatedId")
    public void showDialogue()
    {
        AlertDialog.Builder builder=new AlertDialog.Builder(context);
        View view= LayoutInflater.from(context).inflate(R.layout.action_complete_dialogue,viewGroup,false);
        messageText=view.findViewById(R.id.message_text);
        messageText.setText(message);
        builder.setView(view);
        AlertDialog alertDialog= builder.create();
        alertDialog.show();
        new CountDownTimer(dismissTime, dismissTime) {
            @Override
            public void onTick(long l) {
            }
            @Override
            public void onFinish() {
                alertDialog.dismiss();
            }
        }.start();
    }


}
