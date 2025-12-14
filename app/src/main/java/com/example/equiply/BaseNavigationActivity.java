package com.example.equiply;

import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.example.equiply.admin_activity.AddToolActivity;
import com.example.equiply.admin_activity.AdminDashboardActivity;
import com.example.equiply.admin_activity.ReportActivity;
import com.example.equiply.helper.SessionManager;
import com.example.equiply.shared_activity.ProfileActivity;
import com.example.equiply.shared_activity.ToolListActivity;
import com.example.equiply.student_activity.HistoryActivity;
import com.example.equiply.student_activity.HomeDashboardActivity;
import com.example.equiply.student_activity.NotificationActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseNavigationActivity extends AppCompatActivity {

    protected BottomNavigationView navView;
    private SessionManager session;

    @Override
    public void setContentView(int layoutResID) {
        session = new SessionManager(this);
        View fullView = getLayoutInflater().inflate(R.layout.activity_base_navigation, null);
        FrameLayout activityContainer = fullView.findViewById(R.id.activity_content_container);

        getLayoutInflater().inflate(layoutResID, activityContainer, true);

        super.setContentView(fullView);

        setupBottomNavigation();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (navView != null) {
            int correctMenuId = getNavigationMenuItemId();
            if (navView.getSelectedItemId() != correctMenuId) {
                navView.setSelectedItemId(correctMenuId);
            }
        }
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private void setupBottomNavigation() {
        navView = findViewById(R.id.bottomNavigationView);

        navView.inflateMenu(getMenuResource());

        if (session.isAdmin()){
            setupAdminNavigation(this, navView);
        } else {
            setupStudentNavigation(this, navView);
        }


        int menuId = getNavigationMenuItemId();
        if (menuId != -1) {
            navView.setSelectedItemId(menuId);
        }
    }

    private void setupStudentNavigation(Activity activity, BottomNavigationView navView) {
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.navigation_home) {
                intent = new Intent(activity, HomeDashboardActivity.class);
            } else if (itemId == R.id.navigation_box) {
                intent = new Intent(activity, ToolListActivity.class);
            } else if (itemId == R.id.navigation_history) {
                intent = new Intent(activity, HistoryActivity.class);
            } else if (itemId == R.id.navigation_notification) {
                intent = new Intent(activity, NotificationActivity.class);
            } else if (itemId == R.id.navigation_profile) {
                intent = new Intent(activity, ProfileActivity.class);
            }

            if (intent != null) {
                if (!activity.getClass().getName().equals(intent.getComponent().getClassName())) {
                    if (itemId == R.id.navigation_home) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        activity.startActivity(intent);
                    } else {
                        activity.startActivity(intent);
                        if (!(activity instanceof HomeDashboardActivity)) {
                            activity.finish();
                        }
                    }
                    activity.overridePendingTransition(0, 0);
                }
            }
            return true;
        });
    }

    private void setupAdminNavigation(Activity activity, BottomNavigationView navView) {
        navView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            Intent intent = null;

            if (itemId == R.id.admin_nav_home) {
                intent = new Intent(activity, AdminDashboardActivity.class);
            } else if (itemId == R.id.admin_nav_tools) {
                intent = new Intent(activity, ToolListActivity.class);
            } else if (itemId == R.id.admin_add_item) {
                intent = new Intent(activity, AddToolActivity.class);
            } else if (itemId == R.id.admin_nav_report) {
                intent = new Intent(activity, ReportActivity.class);
            } else if (itemId == R.id.admin_nav_profil) {
                intent = new Intent(activity, ProfileActivity.class);
            }

            if (intent != null) {
                if (!activity.getClass().getName().equals(intent.getComponent().getClassName())) {
                    if (itemId == R.id.admin_nav_home) {
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
                        activity.startActivity(intent);
                    } else {
                        activity.startActivity(intent);
                        if (!(activity instanceof AdminDashboardActivity)) {
                            activity.finish();
                        }
                    }
                    activity.overridePendingTransition(0, 0);
                }
            }
            return true;
        });
    }

    private int getMenuResource() {
        if (session.isAdmin()) {
            return R.menu.admin_menu;
        } else {
            return R.menu.bottom_menu;
        }
    }

    protected abstract int getNavigationMenuItemId();
}