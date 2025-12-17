package com.example.equiply.admin_activity;

import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.equiply.R;
import com.example.equiply.adapter.BorrowRequestsAdapter;
import com.example.equiply.adapter.ReturnRequestsAdapter;
import com.example.equiply.database.BorrowHistoryDA;
import com.example.equiply.database.LendingRequestDA;
import com.example.equiply.database.UserDA;
import com.example.equiply.model.BorrowHistory;
import com.example.equiply.model.LendingRequest;
import com.google.android.material.button.MaterialButton;

public class AdminApprovalActivity extends AppCompatActivity {
    private RecyclerView rvBorrow, rvReturn;
    private TextView tvEmptyBorrow, tvEmptyReturn;
    private BorrowHistoryDA borrowHistoryDA;
    private LendingRequestDA lendingRequestDA;
    private UserDA userDA;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_admin_approval);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        borrowHistoryDA = new BorrowHistoryDA();
        lendingRequestDA = new LendingRequestDA(this);
        userDA = new UserDA();

        rvBorrow = findViewById(R.id.rvBorrowRequests);
        rvReturn = findViewById(R.id.rvReturnRequests);

        tvEmptyBorrow = findViewById(R.id.tvEmptyBorrow);
        tvEmptyReturn = findViewById(R.id.tvEmptyReturn);

        rvBorrow.setLayoutManager(new LinearLayoutManager(this));
        rvReturn.setLayoutManager(new LinearLayoutManager(this));

        loadBorrowRequests();
        loadReturnRequests();
    }

    private void loadBorrowRequests() {
        borrowHistoryDA.getAllPendingRequests(list -> {
            if (list.isEmpty()) {
                tvEmptyBorrow.setVisibility(View.VISIBLE);
                rvBorrow.setVisibility(View.GONE);
                return;
            } else {
                tvEmptyBorrow.setVisibility(View.GONE);
                rvBorrow.setVisibility(View.VISIBLE);
            }

            BorrowRequestsAdapter adapter = new BorrowRequestsAdapter(list, new BorrowRequestsAdapter.ApprovalActionListener() {
                @Override
                public void onApprove(BorrowHistory item) {
                    showCustomBorrowDialog(item);
                }

                @Override
                public void onReject(BorrowHistory item) {

                }
            });
            rvBorrow.setAdapter(adapter);
        });
    }

    private void showCustomBorrowDialog(BorrowHistory item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_admin_action, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
        TextView tvDetail1 = view.findViewById(R.id.tvDetail1);
        TextView tvDetail2 = view.findViewById(R.id.tvDetail2);
        MaterialButton btnApprove = view.findViewById(R.id.btnApprove);
        MaterialButton btnReject = view.findViewById(R.id.btnReject);
        View imageContainer = view.findViewById(R.id.cardImageContainer);

        imageContainer.setVisibility(View.GONE);
        tvTitle.setText("Persetujuan Peminjaman");

        userDA.getUserByID(item.getUserId(), user -> {
            String userName = (user != null) ? user.getName() : "Unknown";
            tvDetail1.setText("Peminjam: " + userName + " (" + item.getUserId() + ")");
            tvDetail2.setText("Alat: " + item.getToolName() + "\nAlasan: " + item.getReason());

            btnApprove.setOnClickListener(v -> {
                borrowHistoryDA.approveRequest(item.getId(), item.getToolId(), userName, success -> {
                    if (success) {
                        Toast.makeText(this, "Peminjaman Disetujui", Toast.LENGTH_SHORT).show();
                        loadBorrowRequests();
                        dialog.dismiss();
                    }
                });
            });

            btnReject.setOnClickListener(v -> {
                borrowHistoryDA.rejectRequest(item.getId(), success -> {
                    if (success) {
                        Toast.makeText(this, "Peminjaman Ditolak", Toast.LENGTH_SHORT).show();
                        loadBorrowRequests();
                        dialog.dismiss();
                    }
                });
            });
        });

        dialog.show();
    }

    private void loadReturnRequests() {
        lendingRequestDA.getAllPendingReturnRequests(list -> {
            if (list.isEmpty()) {
                tvEmptyReturn.setVisibility(View.VISIBLE);
                rvReturn.setVisibility(View.GONE);
                return;
            } else {
                tvEmptyReturn.setVisibility(View.GONE);
                rvReturn.setVisibility(View.VISIBLE);
            }
            ReturnRequestsAdapter adapter = new ReturnRequestsAdapter(list, item -> {
                showCustomReturnDialog(item);
            });
            rvReturn.setAdapter(adapter);
        });
    }

    private void showCustomReturnDialog(LendingRequest item) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        View view = LayoutInflater.from(this).inflate(R.layout.dialog_admin_action, null);
        builder.setView(view);
        AlertDialog dialog = builder.create();

        if (dialog.getWindow() != null) {
            dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        }

        TextView tvTitle = view.findViewById(R.id.tvDialogTitle);
        TextView tvDetail1 = view.findViewById(R.id.tvDetail1);
        TextView tvDetail2 = view.findViewById(R.id.tvDetail2);
        MaterialButton btnApprove = view.findViewById(R.id.btnApprove);
        MaterialButton btnReject = view.findViewById(R.id.btnReject);
        ImageView ivProof = view.findViewById(R.id.ivProofImage);
        View imageContainer = view.findViewById(R.id.cardImageContainer);

        tvTitle.setText("Verifikasi Pengembalian");
        imageContainer.setVisibility(View.VISIBLE);

        Glide.with(this).load(item.getProofImage()).into(ivProof);

        userDA.getUserByID(item.getUserId(), user -> {
            String userName = (user != null) ? user.getName() : "Unknown";
            tvDetail1.setText("Dikembalikan oleh: " + userName);

            String conditionText = "Kondisi: " + item.getCondition();
            tvDetail2.setText(conditionText);

            if ("Rusak".equalsIgnoreCase(item.getCondition())) {
                tvDetail2.setTextColor(Color.RED);
            } else {
                tvDetail2.setTextColor(Color.parseColor("#4CAF50"));
            }

            btnApprove.setOnClickListener(v -> {
                lendingRequestDA.approveReturn(item.getRequestId(), item.getToolId(), item.getUserId(), item.getCondition(), success -> {
                    if (success) {
                        String msg = "Pengembalian Diterima";
                        if ("Rusak".equalsIgnoreCase(item.getCondition())) {
                            msg += " (Status Alat diupdate: Rusak)";
                        }
                        Toast.makeText(this, msg, Toast.LENGTH_LONG).show();
                        loadReturnRequests();
                        dialog.dismiss();
                    }
                });

                borrowHistoryDA.updateHistoryStatus(user.getId(), item.getToolId(), "Returned");
            });

            btnReject.setOnClickListener(v -> {
                lendingRequestDA.rejectReturn(item.getRequestId(), success -> {
                    if (success) {
                        borrowHistoryDA.updateHistoryStatus(user.getId(), item.getToolId(), "Dipinjam");
                        Toast.makeText(this, "Pengembalian Ditolak", Toast.LENGTH_SHORT).show();
                        loadReturnRequests();
                        dialog.dismiss();
                    }
                });
            });
        });

        dialog.show();
    }

}