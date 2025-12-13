package com.example.equiply.shared_activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.equiply.R;
import com.example.equiply.helper.AuthFirebase;
import com.example.equiply.helper.RealtimeDatabaseFirebase;
import com.example.equiply.helper.SessionManager;


public class ProfileActivity extends AppCompatActivity {

    private AuthFirebase auth;
    private RealtimeDatabaseFirebase db;
    private SessionManager session;

    private TextView textName, textRole, textEmail, labelNim, textNim, profileTitle;
    private Button buttonChangePassword, buttonLogout;

    private static final String ROLE_MAHASISWA = "student";
    private static final String ROLE_ADMIN = "Admin";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new RealtimeDatabaseFirebase(this);
        auth = new AuthFirebase(this);
        session = new SessionManager(this);

        initializeViews();

        loadUserProfileData();

        buttonChangePassword.setOnClickListener(v -> handleChangePassword());
        buttonLogout.setOnClickListener(v -> handleLogout());
    }

    private void initializeViews() {
        profileTitle = findViewById(R.id.profile_title);
        textName = findViewById(R.id.text_name);
        textRole = findViewById(R.id.text_role);
        textEmail = findViewById(R.id.text_email);
        labelNim = findViewById(R.id.label_nim);
        textNim = findViewById(R.id.text_nim);

        buttonChangePassword = findViewById(R.id.button_change_password);
        buttonLogout = findViewById(R.id.button_logout);
    }

    private void loadUserProfileData() {
        String uid = session.getUserId();

        if (uid != null) {
            db.getUserByID(uid, user -> {
                if (user != null) {
                    String name = user.getName();
                    String email = user.getEmail();
                    String role = user.getRole();
                    String nim = user.getNim();

                    textName.setText(name != null ? name : "N/A");
                    textRole.setText(role != null ? role : "N/A");
                    textEmail.setText(email != null ? email : "N/A");

                    handleRoleSpecificUI(role, nim);

                } else {
                    Toast.makeText(this, "Profile data not found or user model error.", Toast.LENGTH_SHORT).show();
                }
            });

        } else {
            Toast.makeText(this, "User not logged in.", Toast.LENGTH_SHORT).show();
        }
    }

    private void handleRoleSpecificUI(String role, String nim) {
        if (role != null && role.equalsIgnoreCase(ROLE_MAHASISWA)) {
            textRole.setText("Mahasiswa");
            if (labelNim != null && textNim != null) {
                labelNim.setVisibility(View.VISIBLE);
                textNim.setVisibility(View.VISIBLE);
                textNim.setText(nim != null ? nim : "N/A");
            }
            profileTitle.setText("Profile");

        } else if (role != null && role.equalsIgnoreCase(ROLE_ADMIN)) {
            textRole.setText("Admin");
            if (labelNim != null && textNim != null) {
                labelNim.setVisibility(View.GONE);
                textNim.setVisibility(View.GONE);
            }
            profileTitle.setText("Profile Admin");
        }
    }

    private void handleChangePassword() {
        Intent intent = new Intent(ProfileActivity.this, ChangePasswordActivity.class);
        startActivity(intent);
    }



    private void handleLogout() {
        auth.logout(ProfileActivity.this);
        finish();
        Toast.makeText(this, "Logged out successfully.", Toast.LENGTH_SHORT).show();
    }
}