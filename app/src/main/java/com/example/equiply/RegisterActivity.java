package com.example.equiply;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.equiply.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference mDatabase;
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

        mAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = firebaseDatabase.getReference();


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

        nameET  = findViewById(R.id.name);
        NIMET = findViewById(R.id.NIM);
        emailET = findViewById(R.id.email);
        passwordET = findViewById(R.id.password);
        registerBTN = findViewById(R.id.btnRegister);


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

                mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {
                                    // LOG 1: Konfirmasi blok if dimasuki
                                    Log.d(TAG, "STEP 1: Authentication successful. Entering main block.");

                                    FirebaseUser user = mAuth.getCurrentUser();

                                    // LOG 2: Cek apakah objek FirebaseUser null atau tidak
                                    if (user == null) {
                                        Log.e(TAG, "STEP 2 ERROR: mAuth.getCurrentUser() returned null even after successful auth!");
                                        Toast.makeText(RegisterActivity.this, "Error: Could not get user session.", Toast.LENGTH_LONG).show();
                                        return; // Hentikan eksekusi
                                    }
                                    Log.d(TAG, "STEP 2: FirebaseUser object is not null.");

                                    String uid = user.getUid();
                                    // LOG 3: Cek apakah UID berhasil didapatkan
                                    Log.d(TAG, "STEP 3: Successfully retrieved UID: " + uid);

                                    // Buat objek POJO
                                    User newUser = new User(uid, name, nim, email);
                                    // LOG 4: Cek apakah objek POJO User berhasil dibuat
                                    Log.d(TAG, "STEP 4: POJO 'User' object created successfully.");

                                    // LOG 5: Tepat sebelum memanggil .setValue()
                                    Log.d(TAG, "STEP 5: Preparing to call .setValue() on database path: /users/" + uid);

                                    mDatabase.child("users").child(uid).setValue(newUser).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            // LOG 6: Konfirmasi OnCompleteListener untuk database dijalankan
                                            Log.d(TAG, "STEP 6: Database onComplete listener has been triggered.");
                                            if (task.isSuccessful()) {
                                                // Ini adalah log jika SEMUANYA berhasil
                                                Log.d(TAG, "FINAL SUCCESS: User data saved successfully.");
                                                Toast.makeText(RegisterActivity.this, "Account created and User Data Saved", Toast.LENGTH_SHORT).show();
                                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                                startActivity(intent);
                                                finish();
                                            } else {
                                                // Ini adalah log jika HANYA penyimpanan database yang gagal
                                                Log.e(TAG, "DATABASE FAILURE: Failed to save user data.", task.getException());
                                                Toast.makeText(RegisterActivity.this, "Database Error: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        }
                                    });

                                    // LOG 7: Tepat setelah memanggil .setValue() (menunjukkan bahwa pemanggilan tidak nge-block)
                                    Log.d(TAG, "STEP 7: Call to .setValue() has been dispatched. Waiting for listener...");





                                } else {
                                    Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                    Toast.makeText(RegisterActivity.this, "Authentication failed.",
                                            Toast.LENGTH_SHORT).show();

                                }
                            }
                        });

            }
        });
    }
}