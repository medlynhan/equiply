package com.example.equiply.services;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import androidx.annotation.Nullable;
import com.example.equiply.helper.NotificationDA;
import com.example.equiply.helper.SessionManager;

public class NotificationService extends Service {

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        checkLoans();
        return START_NOT_STICKY;
    }

    private void checkLoans() {
        SessionManager session = new SessionManager(this);
        String userId = session.getUserId();

        if (userId != null) {
            NotificationDA dao = new NotificationDA();
            dao.checkAndNotify(this, userId);
        }

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}