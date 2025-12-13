package com.example.equiply.shared_activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.equiply.LoginActivity;
import com.example.equiply.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText etNewPassword, etConfirmPassword;
    private Button btnSavePassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);

        etNewPassword = findViewById(R.id.etNewPassword);
        etConfirmPassword = findViewById(R.id.etConfirmPassword);
        btnSavePassword = findViewById(R.id.btnSavePassword);

        btnSavePassword.setOnClickListener(v -> updatePassword());
    }

    private void updatePassword() {
        String newPass = etNewPassword.getText().toString().trim();
        String confirmPass = etConfirmPassword.getText().toString().trim();

        if (newPass.isEmpty() || confirmPass.isEmpty()) {
            Toast.makeText(this, "Password tidak boleh kosong", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!newPass.equals(confirmPass)) {
            Toast.makeText(this, "Password tidak sama", Toast.LENGTH_SHORT).show();
            return;
        }

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            user.updatePassword(newPass)
                    .addOnSuccessListener(unused -> {
                        Toast.makeText(this, "Password berhasil diubah", Toast.LENGTH_SHORT).show();

                        // Pindah ke LoginActivity dan hapus activity stack
                        Intent intent = new Intent(ChangePasswordActivity.this, LoginActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finish(); // optional, tapi aman untuk menutup ChangePasswordActivity
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show()
                    );
        }
    }


}
