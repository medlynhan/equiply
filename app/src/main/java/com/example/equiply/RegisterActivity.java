package com.example.equiply;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
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
import com.example.equiply.helper.RealtimeDatabaseFirebase;
import com.example.equiply.student_activity.HomeDashboardActivity;
import com.google.android.material.textfield.TextInputEditText;

public class RegisterActivity extends AppCompatActivity {

    private AuthFirebase auth;
    private RealtimeDatabaseFirebase db;
    private TextInputEditText emailET, passwordET,nameET, NIMET;
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
        db = new RealtimeDatabaseFirebase(this);
        nameET  = findViewById(R.id.name);
        NIMET = findViewById(R.id.NIM);
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
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
                String email, password,name,nim;

                name = String.valueOf(nameET.getText());
                nim = String.valueOf(NIMET.getText());
                email = String.valueOf(emailET.getText());
                password = String.valueOf(passwordET.getText());

                if (TextUtils.isEmpty(name)){
                    Toast.makeText(RegisterActivity.this,"Name must be not null",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(nim)){
                    Toast.makeText(RegisterActivity.this,"NIM must be not null",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(nim.length() != 10){
                    Toast.makeText(RegisterActivity.this,"NIM must be 10 characters",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(email)){
                    Toast.makeText(RegisterActivity.this,"Email must be not null",Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)){
                    Toast.makeText(RegisterActivity.this, "Password must be not null",Toast.LENGTH_SHORT).show();
                    return;
                }

                if(password.length() < 6){
                    Toast.makeText(RegisterActivity.this,"Password must be  at least 6 characters",Toast.LENGTH_SHORT).show();
                    return;
                }

                auth.register(RegisterActivity.this,name,nim,email,password);

            }
        });
    }
}