package com.example.passwordmanager.adapter;

import static android.app.Activity.RESULT_OK;
import static android.content.Context.KEYGUARD_SERVICE;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.KeyguardManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.passwordmanager.R;
import com.example.passwordmanager.model.Password;
import com.example.passwordmanager.ui.MainActivity;
import com.example.passwordmanager.ui.PasswordListFragment;
import com.example.passwordmanager.ui.dialogue.ErrorDialogue;
import com.example.passwordmanager.viewholder.PasswordCardViewHolder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;

public class PasswordCardAdapter extends RecyclerView.Adapter<PasswordCardViewHolder> {

    ArrayList<Password> passwords;
    Context context;
    MainActivity activity;
    FragmentActivity fragmentActivity;
    PasswordListFragment passwordListFragment;
    ClipboardManager clipboardManager;
    public PasswordCardAdapter(MainActivity activity, FragmentActivity fragmentActivity, Context context, PasswordListFragment passwordListFragment, ArrayList<Password> passwords) {
        this.passwords = passwords;
        this.context=context;
        this.activity=activity;
        this.fragmentActivity=fragmentActivity;
        this.passwordListFragment=passwordListFragment;
        clipboardManager= (ClipboardManager) activity.getSystemService(Context.CLIPBOARD_SERVICE);
    }

    @NonNull
    @Override
    public PasswordCardViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater=LayoutInflater.from(parent.getContext());
        View view=layoutInflater.inflate(R.layout.password_card_layout,parent,false);

        return new PasswordCardViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PasswordCardViewHolder holder, int position) {
        Password password=passwords.get(position);
        CountDownTimer countDownTimer=new CountDownTimer(2000,2000) {
            @Override
            public void onTick(long l) {
            }

            @Override
            public void onFinish() {
                holder.messageText.setVisibility(View.GONE);
            }
        };
        if(password.getPassword()!=null){
            holder.passwordText.setText(password.getPassword());
        }
        holder.usernameText.setText(password.getUsername());
        if(password.getPasswordUnlocked())
        {
            holder.passwordText.setTransformationMethod(null);
            password.setPasswordUnlocked(false);
        }
        holder.passwordTextLayout.setEndIconOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    if(holder.passwordText.getTransformationMethod()==null)
                    {
                        holder.passwordText.setTransformationMethod(PasswordTransformationMethod.getInstance());
                    }
                    else
                    {
                        if(password.getPassword()==null) {
                            passwordListFragment.launchLock(password,false);
                        }
                        else holder.passwordText.setTransformationMethod(null);
                    }
                }
        });

        holder.copyUsernameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String username=holder.usernameText.getText().toString();
                copyToClipboard("Username",username);
                holder.messageText.setText(username+" copied");
                holder.messageText.setVisibility(View.VISIBLE);
                countDownTimer.start();
            }
        });

        holder.copyPasswordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(password.getPassword()==null) {
                    passwordListFragment.launchLock(password,true);
                }
                else
                {
                    String password= holder.passwordText.getText().toString();
                    copyToClipboard("Password",password);
                    holder.messageText.setText(password+" copied");
                    holder.messageText.setVisibility(View.VISIBLE);
                    countDownTimer.start();
                }
            }
        });
    }

    public void copyToClipboard(String label,String msg)
    {
        ClipData clipData=ClipData.newPlainText(label,msg);
        clipboardManager.setPrimaryClip(clipData);
    }
    @Override
    public int getItemCount() {
        if (passwords==null) return 0;
        return passwords.size();
    }
}
