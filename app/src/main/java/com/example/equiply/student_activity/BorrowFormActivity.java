package com.example.equiply.student_activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.content.Intent;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.equiply.R;
import com.example.equiply.helper.AuthFirebase;
import com.example.equiply.helper.BorrowRequestDA;
import com.example.equiply.helper.RealtimeDatabaseFirebase;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseUser;

import java.util.Calendar;

public class BorrowFormActivity extends AppCompatActivity {

    private TextView tvToolName;
    private ImageView ivToolImage;
    private TextInputEditText etBorrowDate, etReturnDate, etReason;
    private Button btnSubmit;
    private CheckBox cbAgreement;
    private String toolId, toolName, toolPicture;
    private String userId;
    private BorrowRequestDA borrowRequestDA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_borrow_form_activtiy);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvToolName = findViewById(R.id.tvToolName);
        ivToolImage = findViewById(R.id.ivToolImage);
        etBorrowDate = findViewById(R.id.etBorrowDate);
        etReturnDate = findViewById(R.id.etReturnDate);
        etReason = findViewById(R.id.etReason);
        btnSubmit = findViewById(R.id.btnSubmit);
        cbAgreement = findViewById(R.id.cbAgreement);

        borrowRequestDA = new BorrowRequestDA();

        toolId = getIntent().getStringExtra("TOOL_ID");
        toolName = getIntent().getStringExtra("TOOL_NAME");
        toolPicture = getIntent().getStringExtra("TOOL_PICTURE");
        userId = getIntent().getStringExtra("USER_ID");
        tvToolName.setText(toolName);

        Glide.with(this)
                .load(toolPicture)
                .placeholder(R.drawable.ic_img_placeholder)
                .into(ivToolImage);

        etBorrowDate.setOnClickListener(v -> showDatePicker(etBorrowDate));
        etReturnDate.setOnClickListener(v -> showDatePicker(etReturnDate));

        btnSubmit.setOnClickListener(v -> submitForm());
        findViewById(R.id.fabBack).setOnClickListener(v -> finish());
    }

    private void showDatePicker(TextInputEditText target) {
        Calendar calendar = Calendar.getInstance();

        new DatePickerDialog(this,
                (view, year, month, dayOfMonth) -> {
                    String date = dayOfMonth + "/" + (month + 1) + "/" + year;
                    target.setText(date);
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
        ).show();
    }

    private void submitForm() {
        String borrowDate = etBorrowDate.getText().toString().trim();
        String returnDate = etReturnDate.getText().toString().trim();
        String reason = etReason.getText().toString().trim();

        if (borrowDate.isEmpty() || returnDate.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbAgreement.isChecked()) {
            Toast.makeText(this, "Kamu harus menyetujui pernyataan.", Toast.LENGTH_SHORT).show();
            return;
        }

        borrowRequestDA.addNewRequest(
                toolId,
                toolName,
                userId,
                borrowDate,
                returnDate,
                reason,
                success -> {
                    if (success) {
                        Toast.makeText(this, "Peminjaman berhasil diajukan!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Gagal mengajukan peminjaman!", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
