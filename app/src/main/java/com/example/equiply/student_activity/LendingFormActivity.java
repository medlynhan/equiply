package com.example.equiply.student_activity;

import android.graphics.Insets;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.equiply.R;
import com.example.equiply.database.LendingRequestDA;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class LendingFormActivity extends AppCompatActivity {

    private TextView tvToolName;
    private ImageView ivToolImage, ivPhotoPreview;
    private LinearLayout llPhotoPlaceholder;
    private CardView cvPhotoUpload;
    private RadioGroup rgCondition;
    private RadioButton rbGood, rbDamaged;
    private CheckBox cbConfirm;
    private Button btnSubmitReturn;

    private String toolId, toolName, toolPicture, userId;
    private Uri proofPhotoUri;
    private LendingRequestDA lendingRequestDA;

    private final ActivityResultLauncher<String> pickImage = registerForActivityResult(
            new ActivityResultContracts.GetContent(),
            uri -> {
                if (uri != null) {
                    proofPhotoUri = uri;
                    // Update UI: Hide placeholder, show image
                    ivPhotoPreview.setImageURI(uri);
                    ivPhotoPreview.setAlpha(1.0f); // Remove transparency
                    llPhotoPlaceholder.setVisibility(View.GONE);
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_lending_form);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars()).toPlatformInsets();
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        tvToolName = findViewById(R.id.tvToolName);
        ivToolImage = findViewById(R.id.ivToolImage);

        cvPhotoUpload = findViewById(R.id.cvPhotoUpload);
        ivPhotoPreview = findViewById(R.id.ivPhotoPreview);
        llPhotoPlaceholder = findViewById(R.id.llPhotoPlaceholder);

        rgCondition = findViewById(R.id.rgCondition);
        rbGood = findViewById(R.id.rbGood);
        rbDamaged = findViewById(R.id.rbDamaged);

        cbConfirm = findViewById(R.id.cbConfirm);
        btnSubmitReturn = findViewById(R.id.btnSubmitReturn);

        lendingRequestDA = new LendingRequestDA(this);
        toolId = getIntent().getStringExtra("TOOL_ID");
        toolName = getIntent().getStringExtra("TOOL_NAME");
        toolPicture = getIntent().getStringExtra("TOOL_PICTURE");
        userId = getIntent().getStringExtra("USER_ID");
        tvToolName.setText(toolName);

        Glide.with(this)
                .load(toolPicture)
                .placeholder(R.drawable.ic_img_placeholder)
                .into(ivToolImage);

        // submit button
        btnSubmitReturn.setOnClickListener(v -> submitForm());
        // return button
        findViewById(R.id.fabBack).setOnClickListener(v -> finish());
        // photo upload button
        cvPhotoUpload.setOnClickListener(v -> {
            pickImage.launch("image/*"); // buka gallery
        });
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void submitForm() {
        String condition = "";
        int selectedId = rgCondition.getCheckedRadioButtonId();
        if (selectedId == R.id.rbGood) {
            condition = "Baik";
        } else if (selectedId == R.id.rbDamaged) {
            condition = "Rusak";
        }

        if (proofPhotoUri == null) {
            Toast.makeText(this, "Bukti foto harus diisi.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!cbConfirm.isChecked()) {
            Toast.makeText(this, "Kamu harus menyetujui pernyataan.", Toast.LENGTH_SHORT).show();
            return;
        }

        String returnDate = new SimpleDateFormat("d/M/yyyy", Locale.getDefault()).format(new Date());

        lendingRequestDA.addNewRequest(
                toolId,
                toolName,
                userId,
                condition,
                returnDate,
                proofPhotoUri,
                success -> {
                    if (success) {
                        Toast.makeText(this, "Pengembalian berhasil diajukan!", Toast.LENGTH_LONG).show();
                        finish();
                    } else {
                        Toast.makeText(this, "Gagal mengajukan pengembalian!", Toast.LENGTH_LONG).show();
                    }
                }
        );
    }
}
