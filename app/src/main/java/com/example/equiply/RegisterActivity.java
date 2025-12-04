package com.example.equiply;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.equiply.helper.AuthFirebase;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private AuthFirebase auth;
    private TextInputEditText emailET, passwordET,nameET, NIMET, confirmPassET;
    private Button registerBTN;

    private ImageView backIcon;
    private TextView goToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_register);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = new AuthFirebase(this);
        nameET  = findViewById(R.id.name);
        NIMET = findViewById(R.id.NIM);
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        confirmPassET = findViewById(R.id.confirmPassword);
        registerBTN = findViewById(R.id.btnRegister);


        backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        goToLogin  = findViewById(R.id.goToLogin);
        goToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this,  LoginActivity.class);
                startActivity(intent);
            }
        });


        registerBTN.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hideKeyboard(v);
                setLoadingState(true);

                String email, password, name, nim, confirmPass;

                name = String.valueOf(nameET.getText()).trim();
                nim = String.valueOf(NIMET.getText()).trim();
                email = String.valueOf(emailET.getText()).trim();
                password = String.valueOf(passwordET.getText());
                confirmPass = String.valueOf(confirmPassET.getText());

                if (TextUtils.isEmpty(name)){
                    Toast.makeText(RegisterActivity.this,"Name must be not null",Toast.LENGTH_SHORT).show();
                    setLoadingState(false);
                    return;
                }

                if (TextUtils.isEmpty(nim)){
                    Toast.makeText(RegisterActivity.this,"NIM must be not null",Toast.LENGTH_SHORT).show();
                    setLoadingState(false);
                    return;
                }

                if (nim.length() != 10) {
                    Toast.makeText(RegisterActivity.this,"NIM must be exactly 10 digits",Toast.LENGTH_SHORT).show();
                    setLoadingState(false);
                    return;
                }

                if (!TextUtils.isDigitsOnly(nim)) {
                    Toast.makeText(RegisterActivity.this,"NIM must contain only numbers",Toast.LENGTH_SHORT).show();
                    setLoadingState(false);
                    return;
                }

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this,"Email must be not null",Toast.LENGTH_SHORT).show();
                    setLoadingState(false);
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Password must be not null",Toast.LENGTH_SHORT).show();
                    setLoadingState(false);
                    return;
                }

                if(password.length() < 6){
                    Toast.makeText(RegisterActivity.this,"Password must be at least 6 characters",Toast.LENGTH_SHORT).show();
                    setLoadingState(false);
                    return;
                }

                if (!password.equals(confirmPass)) {
                    Toast.makeText(RegisterActivity.this, "Passwords do not match", Toast.LENGTH_SHORT).show();
                    setLoadingState(false);
                    return;
                }

                auth.register(RegisterActivity.this, name, nim, email, password, new AuthFirebase.AuthCallback() {
                    @Override
                    public void onSuccess() {
                        // Empty
                    }

                    @Override
                    public void onFailure(String message) {
                        setLoadingState(false);
                        Toast.makeText(RegisterActivity.this, "Database Error: " + message, Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            registerBTN.setEnabled(false);
            registerBTN.setText("Loading...");
        } else {
            registerBTN.setEnabled(true);
            registerBTN.setText("Register");
        }
    }
}