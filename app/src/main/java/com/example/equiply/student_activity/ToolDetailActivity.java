package com.example.equiply.student_activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.equiply.R;
import com.example.equiply.helper.AuthFirebase;
import com.example.equiply.database.BorrowHistoryDA;
import com.example.equiply.database.ToolsDA;
import com.example.equiply.model.Tool;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseUser;

public class ToolDetailActivity extends AppCompatActivity {

    private ImageView ivToolImage;
    private TextView tvToolName, tvStatus, tvToolStatus, tvDescription, tvToolId;
    private MaterialButton btnPinjam;
    private MaterialCardView statusBadge;
    private String userId;
    private AuthFirebase auth;
    private BorrowHistoryDA borrowHistoryDA;
    private ToolsDA toolsDA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tool_detail);

        ivToolImage = findViewById(R.id.ivToolImage);
        tvToolName = findViewById(R.id.tvToolName);
        tvStatus = findViewById(R.id.tvStatus);
        tvToolStatus = findViewById(R.id.tvToolStatus);
        tvDescription = findViewById(R.id.tvDescription);
        tvToolId = findViewById(R.id.tvToolId);
        btnPinjam = findViewById(R.id.btnPinjam);
        statusBadge = findViewById(R.id.statusBadge);

        borrowHistoryDA = new BorrowHistoryDA();
        toolsDA = new ToolsDA(this);
        auth = new AuthFirebase();

        String toolId = getIntent().getStringExtra("TOOL_ID");

        if (toolId != null) {
            loadToolDetail(toolId);
        }

        findViewById(R.id.fabBack).setOnClickListener(v -> finish());
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void loadToolDetail(String toolId) {
        toolsDA.getToolById(toolId, tool -> {
            if (tool != null) {
                showToolData(tool);
            }
        });
    }
    private void showToolData(Tool tool) {
        FirebaseUser firebaseUser = auth.getTheCurrentUser();
        if (firebaseUser != null) {
            userId = firebaseUser.getUid();
        } else {
            userId = null;
        }

        tvToolName.setText(tool.getName());
        tvStatus.setText(tool.getStatus());
        tvToolStatus.setText("Kondisi: " + tool.getToolStatus());
        tvDescription.setText(tool.getDescription());
        tvToolId.setText(tool.getId());

        Glide.with(this)
                .load(tool.getImageUrl())
                .placeholder(R.drawable.ic_img_placeholder)
                .into(ivToolImage);

        if (!tool.getStatus().equalsIgnoreCase("tersedia")) {
            btnPinjam.setEnabled(false);
            btnPinjam.setText("Sedang Dipinjam");
            statusBadge.setCardBackgroundColor(Color.parseColor("#F44336"));
            return;
        }

        borrowHistoryDA.hasPendingRequest(userId, tool.getId(), hasPending -> {
            if (hasPending) {
                btnPinjam.setEnabled(false);
                btnPinjam.setText("Menunggu Persetujuan");
                statusBadge.setCardBackgroundColor(Color.parseColor("#FFC107"));
            } else {
                btnPinjam.setEnabled(true);
                btnPinjam.setText("Pinjam");
                statusBadge.setCardBackgroundColor(Color.parseColor("#4CAF50"));

                btnPinjam.setOnClickListener(v -> {
                    Intent intent = new Intent(this, BorrowFormActivity.class);
                    intent.putExtra("TOOL_ID", tool.getId());
                    intent.putExtra("TOOL_NAME", tool.getName());
                    intent.putExtra("TOOL_PICTURE", tool.getImageUrl());
                    intent.putExtra("TOOL_STATUS", tool.getToolStatus());
                    intent.putExtra("USER_ID", userId);
                    overridePendingTransition(0, 0);
                    startActivity(intent);
                });
            }

        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        String toolId = getIntent().getStringExtra("TOOL_ID");
        if (toolId != null) {
            loadToolDetail(toolId);
        }
    }
}
