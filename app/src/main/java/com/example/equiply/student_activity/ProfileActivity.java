package com.example.equiply.student_activity;

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
import com.example.equiply.model.User; // Import the User model
import com.google.firebase.auth.FirebaseAuth;
import com.example.equiply.shared_activity.ChangePasswordActivity;
import android.content.Intent;


public class ProfileActivity extends AppCompatActivity {

    private AuthFirebase auth;
    private RealtimeDatabaseFirebase db;

    // UI Elements
    private TextView textName, textRole, textEmail, labelNim, textNim, profileTitle;
    private Button buttonChangePassword, buttonLogout;

    // Define user roles constants
    private static final String ROLE_MAHASISWA = "student"; // Updated to match your DB 'student' value
    private static final String ROLE_ADMIN = "Admin"; // Use your actual Admin role string

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

        // Initialize Firebase Helpers
        db = new RealtimeDatabaseFirebase(this);
        auth = new AuthFirebase(this);

        // 1. Initialize Views
        initializeViews();

        // 2. Load User Profile Data using your helper method
        loadUserProfileData();

        // 3. Set up Event Listeners
        buttonChangePassword.setOnClickListener(v -> handleChangePassword());
        buttonLogout.setOnClickListener(v -> handleLogout());
    }

    private void initializeViews() {
        profileTitle = findViewById(R.id.profile_title);
        textName = findViewById(R.id.text_name);
        textRole = findViewById(R.id.text_role);
        textEmail = findViewById(R.id.text_email);

        // These fields are only found in the Mahasiswa layout (R.id.label_nim and R.id.text_nim)
        labelNim = findViewById(R.id.label_nim);
        textNim = findViewById(R.id.text_nim);

        buttonChangePassword = findViewById(R.id.button_change_password);
        buttonLogout = findViewById(R.id.button_logout);
    }

    private void loadUserProfileData() {
        // Warning fix: Get the current user safely
        String uid = FirebaseAuth.getInstance().getCurrentUser() != null ?
                FirebaseAuth.getInstance().getCurrentUser().getUid() : null;

        if (uid != null) {
            // **FIXED:** Use your existing getUserByID method from RealtimeDatabaseFirebase
            db.getUserByID(uid, user -> {
                if (user != null) {
                    // Get data from the User model
                    String name = user.getName();
                    String email = user.getEmail();
                    String role = user.getRole();
                    String nim = user.getNim();

                    // Update UI with common data
                    textName.setText(name != null ? name : "N/A");
                    textRole.setText(role != null ? role : "N/A");
                    textEmail.setText(email != null ? email : "N/A");

                    // Handle Role-Specific UI (NIM visibility)
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
        // Ensure you match the role string saved in your Firebase DB (e.g., "student" or "Admin")
        if (role != null && role.equalsIgnoreCase(ROLE_MAHASISWA)) {
            // Mahasiswa: Show NIM fields (if they were included in the current layout)
            if (labelNim != null && textNim != null) {
                labelNim.setVisibility(View.VISIBLE);
                textNim.setVisibility(View.VISIBLE);
                textNim.setText(nim != null ? nim : "N/A");
            }
            // Use String resource for profile title (Good practice)
            profileTitle.setText("Profile");

        } else if (role != null && role.equalsIgnoreCase(ROLE_ADMIN)) {
            // Admin: Hide NIM fields
            if (labelNim != null && textNim != null) {
                labelNim.setVisibility(View.GONE);
                textNim.setVisibility(View.GONE);
            }
            profileTitle.setText("Profile Mahasiswa");
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