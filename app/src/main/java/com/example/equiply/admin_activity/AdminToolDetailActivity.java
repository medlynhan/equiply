package com.example.equiply.admin_activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.equiply.R;
import com.example.equiply.database.ToolsDA;
import com.example.equiply.model.Tool;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AdminToolDetailActivity extends AppCompatActivity {

    private ImageView ivToolImage;
    private TextView tvToolName, tvToolId, tvStatus, tvToolStatus, tvDescription;
    private MaterialCardView statusBadge, conditionBadge;
    private MaterialButton btnEdit, btnDelete;
    private FloatingActionButton fabBack;

    private ToolsDA toolsDA;

    private String toolId;
    private String toolName;
    private String toolDescription;
    private String toolStatus;
    private String toolCondition;
    private String toolImageUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_tool_detail);

        initializeViews();
        toolsDA = new ToolsDA(this);

        getIntentData();
        displayToolData();
        setupButtons();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void initializeViews() {
        ivToolImage = findViewById(R.id.ivToolImage);
        tvToolName = findViewById(R.id.tvToolName);
        tvToolId = findViewById(R.id.tvToolId);
        tvStatus = findViewById(R.id.tvStatus);
        tvToolStatus = findViewById(R.id.tvToolStatus);
        tvDescription = findViewById(R.id.tvDescription);
        statusBadge = findViewById(R.id.statusBadge);
        conditionBadge = findViewById(R.id.conditionBadge);
        btnEdit = findViewById(R.id.btnEdit);
        btnDelete = findViewById(R.id.btnDelete);
        fabBack = findViewById(R.id.fabBack);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        toolId = intent.getStringExtra("TOOL_ID");
        toolName = intent.getStringExtra("TOOL_NAME");
        toolDescription = intent.getStringExtra("TOOL_DESCRIPTION");
        toolStatus = intent.getStringExtra("TOOL_STATUS");
        toolCondition = intent.getStringExtra("TOOL_CONDITION");
        toolImageUrl = intent.getStringExtra("TOOL_IMAGE_URL");
    }

    private void displayToolData() {
        tvToolName.setText(toolName);
        tvToolId.setText("ID: " + toolId);

        tvDescription.setText(toolDescription);

        tvStatus.setText(toolStatus);
        updateStatusBadgeColor(toolStatus);

        tvToolStatus.setText("Kondisi: " + toolCondition);
        updateConditionBadgeColor(toolCondition);

        Glide.with(this)
                .load(toolImageUrl)
                .placeholder(R.drawable.ic_img_placeholder)
                .error(R.drawable.ic_img_placeholder)
                .centerCrop()
                .into(ivToolImage);
    }

    private void updateStatusBadgeColor(String status) {
        if ("Tersedia".equalsIgnoreCase(status)) {
            statusBadge.setCardBackgroundColor(Color.parseColor("#4CAF50"));
        } else if ("Tidak tersedia".equalsIgnoreCase(status)) {
            statusBadge.setCardBackgroundColor(Color.parseColor("#FF9800"));
        } else {
            statusBadge.setCardBackgroundColor(Color.parseColor("#9E9E9E"));
        }
    }

    private void updateConditionBadgeColor(String condition) {
        if ("Baik".equalsIgnoreCase(condition)) {
            conditionBadge.setCardBackgroundColor(Color.parseColor("#2196F3"));
        } else if ("Rusak".equalsIgnoreCase(condition)) {
            conditionBadge.setCardBackgroundColor(Color.parseColor("#F44336"));
        } else {
            conditionBadge.setCardBackgroundColor(Color.parseColor("#9E9E9E"));
        }
    }

    private void setupButtons() {
        fabBack.setOnClickListener(v -> finish());

        btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(AdminToolDetailActivity.this, EditToolActivity.class);
            intent.putExtra("TOOL_ID", toolId);
            intent.putExtra("TOOL_NAME", toolName);
            intent.putExtra("TOOL_DESCRIPTION", toolDescription);
            intent.putExtra("TOOL_STATUS", toolStatus);
            intent.putExtra("TOOL_CONDITION", toolCondition);
            intent.putExtra("TOOL_IMAGE_URL", toolImageUrl);
            startActivity(intent);
        });

        btnDelete.setOnClickListener(v -> showDeleteConfirmationDialog());
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Hapus Alat")
                .setMessage("Apakah Anda yakin ingin menghapus alat \"" + toolName + "\"?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    deleteTool();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void deleteTool() {
        toolsDA.deleteTool(toolId, success -> {
             if (success) {
                 Toast.makeText(this, "Alat berhasil dihapus", Toast.LENGTH_SHORT).show();
                 finish();
             } else {
                 Toast.makeText(this, "Gagal menghapus alat", Toast.LENGTH_SHORT).show();
             }
         });
    }

    private void updateToolUI(Tool tool) {
        if (tool == null) return;
        toolName = tool.getName();
        toolDescription = tool.getDescription();
        toolStatus = tool.getStatus();
        toolCondition = tool.getToolStatus();
        toolImageUrl = tool.getImageUrl();

        tvToolName.setText(toolName);
        tvToolId.setText("ID: " + toolId);
        tvDescription.setText(toolDescription);

        tvStatus.setText(toolStatus);
        updateStatusBadgeColor(toolStatus);

        tvToolStatus.setText("Kondisi: " + toolCondition);
        updateConditionBadgeColor(toolCondition);

        Glide.with(this)
                .load(toolImageUrl)
                .placeholder(R.drawable.ic_img_placeholder)
                .error(R.drawable.ic_img_placeholder)
                .centerCrop()
                .into(ivToolImage);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (toolId != null) {
            toolsDA.getToolById(toolId, this::updateToolUI);
        }
    }
}