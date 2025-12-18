package com.example.equiply.admin_activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.equiply.R;
import com.example.equiply.database.ToolsDA;
import com.example.equiply.shared_activity.ToolListActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase; // <-- Import ini

public class AddToolActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private FloatingActionButton fabBack;
    private MaterialButton uploadPicture, saveToolBtn;
    private ImageView previewImage;
    private TextInputEditText toolNameET, toolDescET;
    private ToolsDA toolsDA;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_tool);

        toolsDA = new ToolsDA(this);
        fabBack = findViewById(R.id.fabBack);
        uploadPicture = findViewById(R.id.uploadPicture);
        saveToolBtn = findViewById(R.id.saveToolBtn);
        previewImage = findViewById(R.id.previewImage);
        toolNameET = findViewById(R.id.toolNameET);
        toolDescET = findViewById(R.id.toolDescET);

        fabBack.setOnClickListener(v -> finish());
        uploadPicture.setOnClickListener(v -> openFileChooser());
        saveToolBtn.setOnClickListener(v -> saveTool());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            previewImage.setImageURI(selectedImageUri);
        }
    }

    private void saveTool() {
        setLoadingState(true);

        String name = toolNameET.getText().toString().trim();
        String description = toolDescET.getText().toString().trim();

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please upload a image", Toast.LENGTH_SHORT).show();
            setLoadingState(false);
            return;
        }
        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please input name and description", Toast.LENGTH_SHORT).show();
            setLoadingState(false);
            return;
        }

        String toolId = FirebaseDatabase.getInstance().getReference("tools").push().getKey();

        if (toolId != null) {
            toolsDA.addNewTools(AddToolActivity.this, selectedImageUri, toolId, name, description,
                    successMsg -> {
                        Toast.makeText(this, successMsg, Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(this, ToolListActivity.class);
                        startActivity(intent);
                        overridePendingTransition(0, 0);
                    },
                    errorMsg -> {
                        setLoadingState(false);
                        Toast.makeText(this, errorMsg, Toast.LENGTH_SHORT).show();
                    }
            );
        }
    }

    private void setLoadingState(boolean isLoading) {
        if (isLoading) {
            saveToolBtn.setEnabled(false);
            saveToolBtn.setText("Saving...");
        } else {
            saveToolBtn.setEnabled(true);
            saveToolBtn.setText("Save Tool");
        }
    }
}
