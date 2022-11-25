package com.example.passwordmanager.viewholder;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.airbnb.lottie.LottieAnimationView;
import com.example.passwordmanager.R;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class PasswordCardViewHolder extends RecyclerView.ViewHolder {
    public TextInputLayout usernameTextLayout,passwordTextLayout;
    public TextInputEditText usernameText,passwordText;
    public ImageView copyUsernameButton,copyPasswordButton;
    public TextView messageText;

    public PasswordCardViewHolder(@NonNull View itemView) {
        super(itemView);

        usernameTextLayout=itemView.findViewById(R.id.username_text_layout);
        passwordTextLayout=itemView.findViewById(R.id.password_text_layout);
        usernameText=itemView.findViewById(R.id.username_text);
        passwordText=itemView.findViewById(R.id.password_text);
        copyPasswordButton=itemView.findViewById(R.id.copy_password_icon);
        copyUsernameButton=itemView.findViewById(R.id.copy_username_icon);
        messageText= itemView.findViewById(R.id.card_footer_text);

    }
}
