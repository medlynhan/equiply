package com.example.equiply;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.equiply.helper.AuthFirebase;
import com.example.equiply.student_activity.HomeDashboardActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity {

    private AuthFirebase auth;
    private ImageView backIcon;
    private TextView goToRegister;
    private TextInputEditText emailET, passwordET;
    private MaterialButton loginBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        auth = new AuthFirebase();
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);

        backIcon = findViewById(R.id.backIcon);
        backIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });


        goToRegister  = findViewById(R.id.goToRegister);
        goToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this,  RegisterActivity.class);
                overridePendingTransition(0, 0);
                startActivity(intent);
                finish();
            }
        });


        loginBtn = findViewById(R.id.loginBtn);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setLoadingState(true);
                hideKeyboard(v);

                String email, password;

                email = String.valueOf(emailET.getText()).trim();
                password = String.valueOf(passwordET.getText());

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(LoginActivity.this,"Enter email",Toast.LENGTH_SHORT).show();
                    setLoadingState(false);
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(LoginActivity.this, "Enter password",Toast.LENGTH_SHORT).show();
                    setLoadingState(false);
                    return;
                }

                auth.login(LoginActivity.this, email, password, new AuthFirebase.AuthCallback() {
                    @Override
                    public void onSuccess() {
                        // Nothing because of Success
                    }

                    @Override
                    public void onFailure(String message) {
                        setLoadingState(false);
                        Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
                    }
                });
            }

        });

    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void hideKeyboard(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            loginBtn.setEnabled(false);
            loginBtn.setText("Loading...");
        } else {
            loginBtn.setEnabled(true);
            loginBtn.setText("Login");
        }
    }


}