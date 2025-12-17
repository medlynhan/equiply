package com.example.equiply.student_activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.equiply.BaseNavigationActivity;
import com.example.equiply.R;
import com.example.equiply.adapter.StudentBorrowedAdapter;
import com.example.equiply.database.BorrowHistoryDA;
import com.example.equiply.database.NotificationDA;
import com.example.equiply.database.ToolsDA;
import com.example.equiply.helper.QRCodeScanner;
import com.example.equiply.helper.SessionManager;
import com.example.equiply.model.BorrowHistory;
import com.example.equiply.shared_activity.ToolListActivity;
import com.google.zxing.integration.android.IntentIntegrator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class HomeDashboardActivity extends BaseNavigationActivity {
    private SessionManager session;
    private ToolsDA toolsDA;
    private BorrowHistoryDA borrowHistoryDA;
    private TextView userName, tvTime, tvDate;
    private TextView tvActiveLoanCount, tvDueTodayCount;
    private TextView tvSeeAllItems, tvEmpty;
    private StudentBorrowedAdapter adapter;
    private RecyclerView rvBorrowedItems;
    private ArrayList<BorrowHistory> borrowHistories;
    private Button btnSearchTools, btnScanQR;
    private static final int NOTIFICATION_PERMISSION_CODE = 101;
    private final Handler timeHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_dashboard);

        session = new SessionManager(this);
        toolsDA = new ToolsDA(this);
        borrowHistoryDA = new BorrowHistoryDA();
        borrowHistories = new ArrayList<>();

        userName = findViewById(R.id.userName);
        tvTime = findViewById(R.id.tvTime);
        tvDate = findViewById(R.id.tvDate);

        tvActiveLoanCount = findViewById(R.id.activeLoanCount);
        tvDueTodayCount = findViewById(R.id.dueTodayCount);

        tvSeeAllItems = findViewById(R.id.tvSeeAllItems);
        tvEmpty = findViewById(R.id.tvEmptyActiveLoans);

        rvBorrowedItems = findViewById(R.id.rvActiveItems);

        btnSearchTools = findViewById(R.id.btnSearchTools);
        btnScanQR = findViewById(R.id.btnScanQR);

        userName.setText(session.getName());

        startLiveClock();
        setupRecyclerView();
        
        tvSeeAllItems.setOnClickListener(v -> redirectToHistory());

        btnSearchTools.setOnClickListener(v -> redirectToToolList());
        setupQrScanner();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadStudentData();
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvBorrowedItems.setLayoutManager(layoutManager);

        adapter = new StudentBorrowedAdapter(this, borrowHistories);
        rvBorrowedItems.setAdapter(adapter);
    }

    private void loadStudentData() {
        String userId = session.getUserId();

        borrowHistoryDA.getHistoryByUserId(userId, borrowHistories -> {
            if (this.borrowHistories == null) this.borrowHistories = new ArrayList<>();

            this.borrowHistories.clear();

            int borrowedCounter = 0;
            int dueTodayCounter = 0;

            for (BorrowHistory history : borrowHistories) {
                String returnDate = history.getReturnDate();

                if ("Approved".equalsIgnoreCase(history.getStatus())
                        || "Dipinjam".equalsIgnoreCase(history.getStatus())
                        || "pending_return".equalsIgnoreCase(history.getStatus())) {
                    borrowedCounter++;

                    if (isOverdue(returnDate)) {
                        dueTodayCounter++;
                    }

                    this.borrowHistories.add(history);
                }
            }

            tvActiveLoanCount.setText(String.valueOf(borrowedCounter));
            tvDueTodayCount.setText(String.valueOf(dueTodayCounter));

            if (this.borrowHistories.isEmpty()) {
                rvBorrowedItems.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            } else {
                rvBorrowedItems.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
            }

            adapter.notifyDataSetChanged();
        });
    }

    private boolean isOverdue(String returnDate) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        try {
            Date due = sdf.parse(returnDate);
            Date today = sdf.parse(sdf.format(new Date()));

            return today.after(due);

        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        handleNotification();
    }

    @Override
    protected int getNavigationMenuItemId() {
        return R.id.navigation_home;
    }

    private void handleNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if(ActivityCompat.checkSelfPermission(getApplicationContext(),
                    Manifest.permission.POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.POST_NOTIFICATIONS},
                        NOTIFICATION_PERMISSION_CODE);
            } else {
                checkForDueLoan();
            }
        } else {
            checkForDueLoan();
        }
    }

    private void checkForDueLoan() {
        String userId = session.getUserId();
        if (userId != null) {
            NotificationDA dao = new NotificationDA();
            dao.checkAndNotify(this, userId);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == NOTIFICATION_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                checkForDueLoan();
            } else {
                checkForDueLoan();
                Toast.makeText(this, "Notifications disabled. Check history for alerts.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void startLiveClock() {
        timeHandler.post(new Runnable() {
            @Override
            public void run() {
                String time = new SimpleDateFormat("h:mm a", Locale.getDefault())
                        .format(new Date());
                String date = new SimpleDateFormat("dd MMMM yyyy", Locale.getDefault())
                        .format(new Date());

                tvTime.setText(time);
                tvDate.setText(date);

                timeHandler.postDelayed(this, 1000);
            }
        });
    }

    private void setupQrScanner() {
        btnScanQR.setOnClickListener(v -> {
            IntentIntegrator intentIntegrator = new IntentIntegrator(this);

            intentIntegrator.setCaptureActivity(QRCodeScanner.class);
            intentIntegrator.setPrompt(" ");
            intentIntegrator.setOrientationLocked(false);
            intentIntegrator.setBeepEnabled(false);
            intentIntegrator.initiateScan();
        });
    }

    private void redirectToHistory() {
        Intent intent = new Intent(this, HistoryActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }

    private void redirectToToolList() {
        Intent intent = new Intent(this, ToolListActivity.class);
        startActivity(intent);
        overridePendingTransition(0, 0);
    }
}