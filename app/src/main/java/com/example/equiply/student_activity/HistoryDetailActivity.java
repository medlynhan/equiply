package com.example.equiply.student_activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.example.equiply.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

public class HistoryDetailActivity extends AppCompatActivity {

    private ImageView ivToolImage;
    private TextView tvToolName, tvStatus, tvBorrowDate, tvReturnDate, tvReason;
    private MaterialButton btnAction;
    private MaterialCardView statusBadge, actionButtonCard;

    // Data variables
    private String toolId, toolName, toolImage, userId;
    private String status, borrowDate, returnDate, reason;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_history_detail); // Make sure this matches your XML filename

        initViews();

        // ambil data dari HistoryAdapter
        getIntentData();

        // set data to UI
        setupUI();

        // ngehandle back button
        findViewById(R.id.fabBack).setOnClickListener(v -> finish());
    }

    private void initViews() {
        ivToolImage = findViewById(R.id.ivToolImage);
        tvToolName = findViewById(R.id.tvToolName);
        tvStatus = findViewById(R.id.tvStatus);
        tvBorrowDate = findViewById(R.id.tvBorrowDate);
        tvReturnDate = findViewById(R.id.tvReturnDate);
        tvReason = findViewById(R.id.tvReason);

        btnAction = findViewById(R.id.btnAction);
        statusBadge = findViewById(R.id.statusBadge);
        actionButtonCard = findViewById(R.id.actionButtonCard);
    }

    private void getIntentData() {
        Intent intent = getIntent();
        toolId = intent.getStringExtra("TOOL_ID");
        toolName = intent.getStringExtra("TOOL_NAME");
        toolImage = intent.getStringExtra("TOOL_IMAGE");
        userId = intent.getStringExtra("USER_ID");

        status = intent.getStringExtra("STATUS");
        borrowDate = intent.getStringExtra("BORROW_DATE");
        returnDate = intent.getStringExtra("RETURN_DATE");
        reason = intent.getStringExtra("REASON");
    }

    private void setupUI() {
        tvToolName.setText(toolName);
        tvBorrowDate.setText(borrowDate);
        tvReturnDate.setText(returnDate);
        tvReason.setText(reason);

        // Load Image
        Glide.with(this)
                .load(toolImage)
                .placeholder(R.drawable.ic_img_placeholder)
                .into(ivToolImage);

        // Configure Status and Button based on state
        configureStatusLogic();
    }

    private void configureStatusLogic() {
        // Normalize status string to handle potential case differences
        String currentStatus = (status != null) ? status.toLowerCase() : "";

        if (currentStatus.equals("dipinjam") || currentStatus.equals("approved")) {
            // Case 1: Currently Borrowed -> Show "Kembalikan" button
            tvStatus.setText("Dipinjam");
            statusBadge.setCardBackgroundColor(Color.parseColor("#FF9800")); // Orange

            btnAction.setText("Kembalikan Alat");
            // btnAction.setIconResource(R.drawable.ic_back); // Uncomment if you have an icon
            btnAction.setEnabled(true);
            btnAction.setBackgroundColor(Color.parseColor("#80D4E7")); // Light Blue

            btnAction.setOnClickListener(v -> {
                Intent intent = new Intent(this, LendingFormActivity.class);
                intent.putExtra("TOOL_ID", toolId);
                intent.putExtra("TOOL_NAME", toolName);
                intent.putExtra("TOOL_PICTURE", toolImage);
                intent.putExtra("USER_ID", userId);
                startActivity(intent);
            });

        } else if (currentStatus.equals("pending_return")) {
            // Case 2: Return Requested -> Show "Menunggu" (Disabled)
            tvStatus.setText("Menunggu Konfirmasi");
            statusBadge.setCardBackgroundColor(Color.parseColor("#FFC107")); // Amber

            btnAction.setText("Menunggu Verifikasi Admin");
            btnAction.setIcon(null);
            btnAction.setEnabled(false);
            btnAction.setBackgroundColor(Color.LTGRAY);

        } else if (currentStatus.equals("dikembalikan") || currentStatus.equals("returned")) {
            // Case 3: Finished -> Hide Button
            tvStatus.setText("Dikembalikan");
            statusBadge.setCardBackgroundColor(Color.parseColor("#4CAF50")); // Green

            actionButtonCard.setVisibility(View.GONE); // Hide the entire bottom card
        } else {
            // Fallback for other statuses (e.g. pending borrow request)
            tvStatus.setText(status);
            statusBadge.setCardBackgroundColor(Color.GRAY);
            actionButtonCard.setVisibility(View.GONE);
        }
    }
}