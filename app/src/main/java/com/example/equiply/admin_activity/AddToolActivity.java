package com.example.equiply.admin_activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.equiply.R;
import com.example.equiply.helper.RealtimeDatabaseFirebase;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.database.FirebaseDatabase; // <-- Import ini

public class AddToolActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;
    private MaterialButton uploadPicture, saveToolBtn;
    private ImageView previewImage;
    private TextInputEditText toolNameET, toolDescET;

    private RealtimeDatabaseFirebase db;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_tool);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = new RealtimeDatabaseFirebase(this);
        uploadPicture = findViewById(R.id.uploadPicture);
        saveToolBtn = findViewById(R.id.saveToolBtn);
        previewImage = findViewById(R.id.previewImage);
        toolNameET = findViewById(R.id.toolNameET);
        toolDescET = findViewById(R.id.toolDescET);


        uploadPicture.setOnClickListener(v -> openFileChooser());
        saveToolBtn.setOnClickListener(v -> saveTool());
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
        String name = toolNameET.getText().toString().trim();
        String description = toolDescET.getText().toString().trim();

        if (selectedImageUri == null) {
            Toast.makeText(this, "Please upload a image", Toast.LENGTH_SHORT).show();
            return;
        }
        if (name.isEmpty() || description.isEmpty()) {
            Toast.makeText(this, "Please input name and description", Toast.LENGTH_SHORT).show();
            return;
        }

        //generate ID unik untuk tool baru menggunakan Firebase
        String toolId = FirebaseDatabase.getInstance().getReference("tools").push().getKey();

        if (toolId != null) {
            db.addNewTools(AddToolActivity.this, selectedImageUri, toolId, name, description);
        }
    }
}
