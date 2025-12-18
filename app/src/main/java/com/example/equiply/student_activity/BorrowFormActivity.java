package com.example.equiply.student_activity;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.equiply.R;
import com.example.equiply.database.BorrowHistoryDA;
import com.example.equiply.model.BorrowHistory;
import com.google.android.material.textfield.TextInputEditText;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class BorrowFormActivity extends AppCompatActivity {

    private TextView tvToolName;
    private ImageView ivToolImage;
    private TextInputEditText etBorrowDate, etReturnDate, etReason;
    private Button btnSubmit;
    private CheckBox cbAgreement;
    private String toolId, toolName, toolPicture, toolStatus;
    private String userId;
    private BorrowHistoryDA borrowHistoryDA;

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

        borrowHistoryDA = new BorrowHistoryDA();

        toolId = getIntent().getStringExtra("TOOL_ID");
        toolName = getIntent().getStringExtra("TOOL_NAME");
        toolPicture = getIntent().getStringExtra("TOOL_PICTURE");
        toolStatus = getIntent().getStringExtra("TOOL_STATUS");
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

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
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

        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());

        String requestAt = sdf.format(Calendar.getInstance().getTime());

        if (borrowDate.isEmpty() || returnDate.isEmpty() || reason.isEmpty()) {
            Toast.makeText(this, "Semua field wajib diisi.", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Date dateBorrow = sdf.parse(borrowDate);
            Date dateReturn = sdf.parse(returnDate);
            Date dateToday = sdf.parse(requestAt);

            if (dateBorrow.before(dateToday)) {
                Toast.makeText(this, "Tanggal pinjam tidak boleh di masa lalu.", Toast.LENGTH_SHORT).show();
                return;
            }

            if (dateReturn.before(dateBorrow)) {
                Toast.makeText(this, "Tanggal kembali tidak boleh sebelum tanggal pinjam.", Toast.LENGTH_SHORT).show();
                return;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(this, "Format tanggal salah. Gunakan dd/MM/yyyy", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbAgreement.isChecked()) {
            Toast.makeText(this, "Kamu harus menyetujui pernyataan.", Toast.LENGTH_SHORT).show();
            return;
        }

         borrowHistoryDA.addBorrowHistory(
                new BorrowHistory(
                        userId,
                        toolId,
                        toolName,
                        reason,
                        "Pending",
                        toolPicture,
                        requestAt,
                        borrowDate,
                        returnDate,
                        toolStatus,
                        System.currentTimeMillis()
                ), success -> {
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
