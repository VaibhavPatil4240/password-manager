package com.example.passwordmanager.ui.dialogue;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.os.CountDownTimer;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.example.passwordmanager.R;

public class MessageDialogue {
    String message;
    Context context;
    ViewGroup viewGroup;
    TextView messageText;
    Button okButton;
    public MessageDialogue(Context context,ViewGroup viewGroup,String message) {
        this.message = message;
        this.context=context;
        this.viewGroup=viewGroup;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    @SuppressLint("MissingInflatedId")
    public void showDialogue() {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        View view = LayoutInflater.from(context).inflate(R.layout.message_dialogue, viewGroup, false);
        messageText = view.findViewById(R.id.message_text);
        okButton= view.findViewById(R.id.ok_button);
        messageText.setText(message);
        builder.setView(view);
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }
}
