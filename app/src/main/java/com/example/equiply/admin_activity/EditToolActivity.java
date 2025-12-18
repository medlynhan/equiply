package com.example.equiply.admin_activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.example.equiply.R;
import com.example.equiply.database.ToolsDA;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

public class EditToolActivity extends AppCompatActivity {

    private ImageView ivToolPreview;
    private TextInputEditText etToolName, etDescription;
    private AutoCompleteTextView actvStatus, actvCondition;
    private MaterialButton btnSave, btnChangeImage;
    private FloatingActionButton fabBack;

    private ToolsDA toolsDA;
    private ActivityResultLauncher<Intent> imagePickerLauncher;
    private String toolId;
    private String currentImageUrl;
    private Uri newImageUri;

    private static final String[] STATUS_OPTIONS = {"Tersedia", "Tidak tersedia"};
    private static final String[] CONDITION_OPTIONS = {"Baik", "Rusak"};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_edit_tool);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        initializeViews();
        setupImagePicker();
        toolsDA = new ToolsDA(this);

        loadToolData();
        setupDropdowns();
        setupButtons();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void initializeViews() {
        ivToolPreview = findViewById(R.id.ivToolPreview);
        etToolName = findViewById(R.id.etToolName);
        etDescription = findViewById(R.id.etDescription);
        actvStatus = findViewById(R.id.actvStatus);
        actvCondition = findViewById(R.id.actvCondition);
        btnSave = findViewById(R.id.btnSave);
        btnChangeImage = findViewById(R.id.btnChangeImage);
        fabBack = findViewById(R.id.fabBack);
    }

    private void setupImagePicker() {
        imagePickerLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == RESULT_OK && result.getData() != null) {
                        newImageUri = result.getData().getData();
                        Glide.with(this)
                                .load(newImageUri)
                                .centerCrop()
                                .into(ivToolPreview);
                    }
                }
        );
    }

    private void loadToolData() {
        Intent intent = getIntent();
        toolId = intent.getStringExtra("TOOL_ID");
        String toolName = intent.getStringExtra("TOOL_NAME");
        String description = intent.getStringExtra("TOOL_DESCRIPTION");
        String status = intent.getStringExtra("TOOL_STATUS");
        String condition = intent.getStringExtra("TOOL_CONDITION");
        currentImageUrl = intent.getStringExtra("TOOL_IMAGE_URL");

        etToolName.setText(toolName);
        etDescription.setText(description);
        actvStatus.setText(status, false);
        actvCondition.setText(condition, false);

        Glide.with(this)
                .load(currentImageUrl)
                .placeholder(R.drawable.ic_img_placeholder)
                .centerCrop()
                .into(ivToolPreview);
    }

    private void setupDropdowns() {
        ArrayAdapter<String> statusAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                STATUS_OPTIONS
        );
        actvStatus.setAdapter(statusAdapter);

        ArrayAdapter<String> conditionAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_dropdown_item_1line,
                CONDITION_OPTIONS
        );
        actvCondition.setAdapter(conditionAdapter);
    }

    private void setupButtons() {
        fabBack.setOnClickListener(v -> finish());
        btnChangeImage.setOnClickListener(v -> openImagePicker());
        btnSave.setOnClickListener(v -> saveChanges());
    }

    private void openImagePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        imagePickerLauncher.launch(intent);
    }

    private void saveChanges() {
        String name = etToolName.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        String status = actvStatus.getText().toString().trim();
        String condition = actvCondition.getText().toString().trim();

        if (name.isEmpty()) {
            etToolName.setError("Nama alat tidak boleh kosong");
            etToolName.requestFocus();
            return;
        }

        if (description.isEmpty()) {
            etDescription.setError("Deskripsi tidak boleh kosong");
            etDescription.requestFocus();
            return;
        }

        if (status.isEmpty()) {
            Toast.makeText(this, "Pilih status ketersediaan", Toast.LENGTH_SHORT).show();
            return;
        }

        if (condition.isEmpty()) {
            Toast.makeText(this, "Pilih kondisi alat", Toast.LENGTH_SHORT).show();
            return;
        }

        btnSave.setEnabled(false);
        btnSave.setText("Menyimpan...");

        if (newImageUri != null) {
            updateToolWithNewImage(name, description, status, condition);
        } else {
            updateTool(name, description, status, condition, currentImageUrl);
        }
    }

    private void updateToolWithNewImage(String name, String description, String status, String condition) {
        toolsDA.updateToolWithNewImage(this, toolId, newImageUri, name, description, status, condition,
                imageUrl -> {
                    Toast.makeText(this, "Alat berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    finish();
                },
                errorMessage -> {
                    Toast.makeText(this, "Gagal mengunggah gambar: " + errorMessage, Toast.LENGTH_SHORT).show();
                    btnSave.setEnabled(true);
                    btnSave.setText("Simpan Perubahan");
                }
        );
    }

    private void updateTool(String name, String description, String status, String condition, String imageUrl) {
        toolsDA.updateTool(toolId, name, description, status, condition, imageUrl, success -> {
            if (success) {
                Toast.makeText(this, "Alat berhasil diperbarui", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Gagal memperbarui alat", Toast.LENGTH_SHORT).show();
                btnSave.setEnabled(true);
                btnSave.setText("Simpan Perubahan");
            }
        });
    }
}