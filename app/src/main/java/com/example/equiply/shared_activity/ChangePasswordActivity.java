package com.example.equiply.shared_activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.equiply.LoginActivity;
import com.example.equiply.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private TextInputEditText etCurrentPassword, etNewPassword, etConfirmPassword;
    private MaterialButton btnSavePassword, btnCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etCurrentPassword = findViewById(R.id.etCurrPass);
        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSavePassword = findViewById(R.id.btnSavePassword);
        btnCancel = findViewById(R.id.btnCancel);

        btnSavePassword.setOnClickListener(v -> updatePassword());
        btnCancel.setOnClickListener(v -> finish());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void updatePassword() {
        String currPass = etCurrentPassword.getText().toString().trim();
        String newPass = etNewPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        if (currPass.isEmpty() || newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Password baru tidak sama", Toast.LENGTH_SHORT).show();
            return;
        }

        if (currPass.equals(newPass)) {
            Toast.makeText(this, "Password baru tidak boleh sama dengan password saat ini", Toast.LENGTH_SHORT).show();
            return;
        }

        if (newPass.length() < 6) {
            Toast.makeText(this, "Password minimal 6 karakter", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null || user.getEmail() == null) {
            Toast.makeText(this, "Session berakhir, silakan login ulang", Toast.LENGTH_SHORT).show();
            redirectToLogin();
            return;
        }

        setLoadingState(true);

        String email = user.getEmail();

        AuthCredential credential = EmailAuthProvider.getCredential(email, currPass);

        user.reauthenticate(credential)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        user.updatePassword(newPass)
                                .addOnSuccessListener(unused -> {
                                    Toast.makeText(this,
                                            "Password berhasil diubah. Silakan login ulang.",
                                            Toast.LENGTH_SHORT).show();

                                    FirebaseAuth.getInstance().signOut();
                                    redirectToLogin();
                                })
                                .addOnFailureListener(e -> {
                                    setLoadingState(false);
                                    Toast.makeText(this,
                                            e.getMessage(),
                                            Toast.LENGTH_SHORT).show();
                                });
                    } else {
                        setLoadingState(false);
                        Toast.makeText(this,
                                "Password Saat ini Salah",
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
    private void setLoadingState(boolean isProcessing) {
        if (isProcessing) {
            btnSavePassword.setEnabled(false);
            btnSavePassword.setText("Saving...");
        } else {
            btnSavePassword.setEnabled(true);
            btnSavePassword.setText("Save");
        }
    }

    private void redirectToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }


}
