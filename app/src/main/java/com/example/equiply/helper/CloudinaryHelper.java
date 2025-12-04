package com.example.equiply.helper;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.cloudinary.android.MediaManager;
import com.cloudinary.android.callback.ErrorInfo;
import com.cloudinary.android.callback.UploadCallback;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class CloudinaryHelper {

    private static final String TAG = "Cloudinary";
    private static final String UPLOAD_PRESET = "equiply_uploads";
    private static boolean isInitialized = false;
    public CloudinaryHelper(Context context) {

        if (!isInitialized) {

            Map<String, String> config = new HashMap<>();
            config.put("cloud_name", "dozjnpil7");
            config.put("api_key", "489187349717296");

            MediaManager.init(context.getApplicationContext(), config);
            isInitialized = true;
        }
    }

    public void uploadImage(Uri imageUri, Consumer<String> onSuccess, Consumer<String> onError) {
        MediaManager.get().upload(imageUri)
                .unsigned(UPLOAD_PRESET)
                .callback(new UploadCallback() {
                    @Override
                    public void onStart(String requestId) {}

                    @Override
                    public void onProgress(String requestId, long bytes, long totalBytes) {}

                    @Override
                    public void onSuccess(String requestId, Map resultData) {
                        String imageUrl = (String) resultData.get("secure_url");
                        if (imageUrl == null) {
                            imageUrl = (String) resultData.get("url"); // Fallback
                        }

                        if (imageUrl != null) {
                            onSuccess.accept(imageUrl); // Panggil callback sukses
                        } else {
                            onError.accept("URL gambar tidak ditemukan pada respons."); // Panggil callback error
                        }
                    }

                    @Override
                    public void onError(String requestId, ErrorInfo error) {
                        onError.accept(error.getDescription()); // Panggil callback error
                    }

                    @Override
                    public void onReschedule(String requestId, ErrorInfo error) { }
                })
                .dispatch();
        }


}
