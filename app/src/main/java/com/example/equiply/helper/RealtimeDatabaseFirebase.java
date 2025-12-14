package com.example.equiply.helper;

import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.equiply.model.Tool;
import com.example.equiply.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.function.Consumer;

public class RealtimeDatabaseFirebase {

    private final DatabaseReference mDatabase;
    private final CloudinaryHelper cloudinaryHelper;

    public RealtimeDatabaseFirebase(Context context) {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
        this.cloudinaryHelper = new CloudinaryHelper(context);
    }
    public DatabaseReference getReference(String path) {
        // Since mDatabase is the root reference, we use .child(path)
        return mDatabase.child(path);
    }

    public void addNewUser(Context context,String uid, String name, String nim, String email){
        User newUser = new User(uid, name, nim, email);
        newUser.setRole("student");
        mDatabase.child("users").child(uid).setValue(newUser).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                Toast.makeText(context, "Account created and User Data Saved", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(context, "Failed Saved User Data", Toast.LENGTH_SHORT).show();
            }
        });

    }

    public void getUserByID(String id, Consumer<User> callback){
        mDatabase.child("users").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    User user =  snapshot.getValue(User.class);
                    callback.accept(user);
                }else{
                    callback.accept(null);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                callback.accept(null);
            }
        });

    }

    public void addNewTools(Context context, Uri imageUri, String id, String name, String description
                , Consumer<String> onSuccess, Consumer<String> onError) {

        cloudinaryHelper.uploadImage(imageUri, imageUrl -> {
                    Toast.makeText(context, "Gambar berhasil diunggah, menyimpan data...", Toast.LENGTH_SHORT).show();
                    Tool newTool = new Tool(id, name, description, "Tersedia", imageUrl,"Baik");

                    saveTool(newTool, success -> {
                        if (success) {
                            onSuccess.accept("Tool baru berhasil ditambahkan!");
                        } else {
                            onError.accept("Gagal menyimpan data tool ke database.");
                        }
                    });
                },
                errorMessage -> {
                    onError.accept("Gagal Menunggah Gambar...");
                }
        );
    }

    private void saveTool(Tool newTool, Consumer<Boolean> callback) {
        mDatabase.child("tools").child(newTool.getId()).setValue(newTool).addOnCompleteListener(task -> {
            callback.accept(task.isSuccessful());
        });
    }

    public void getAllTools(Consumer<ArrayList<Tool>> callback) {
        mDatabase.child("tools").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                ArrayList<Tool> toolList = new ArrayList<>();
                if (dataSnapshot.exists()) {
                    for (DataSnapshot toolSnapshot : dataSnapshot.getChildren()) {
                        Tool tool = toolSnapshot.getValue(Tool.class);
                        if (tool != null) {
                            toolList.add(tool);
                        }
                    }
                }
                callback.accept(toolList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.accept(new ArrayList<>());
            }
        });
    }

    public void getToolById(String toolId, Consumer<Tool> callback) {
        mDatabase.child("tools").child(toolId).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        Tool tool = snapshot.getValue(Tool.class);
                        callback.accept(tool);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.accept(null);
                    }
                });
    }

    public void deleteTool(String toolId, Consumer<Boolean> callback) {
        mDatabase.child("tools").child(toolId).removeValue()
                .addOnCompleteListener(task -> {
                    callback.accept(task.isSuccessful());
                });
    }

    public void updateTool(String toolId, String name, String description,
                           String status, String condition, String imageUrl,
                           Consumer<Boolean> callback) {
        Tool updatedTool = new Tool(toolId, name, description, status, imageUrl, condition);
        mDatabase.child("tools").child(toolId).setValue(updatedTool)
                .addOnCompleteListener(task -> {
                    callback.accept(task.isSuccessful());
                });
    }

    public void updateToolWithNewImage(Context context, String toolId, Uri newImageUri,
                                       String name, String description,
                                       String status, String condition,
                                       Consumer<String> onSuccess,
                                       Consumer<String> onError) {
        cloudinaryHelper.uploadImage(newImageUri,
                imageUrl -> {
                    updateTool(toolId, name, description, status, condition, imageUrl, success -> {
                        if (success) {
                            onSuccess.accept(imageUrl);
                        } else {
                            onError.accept("Gagal menyimpan data tool ke database");
                        }
                    });
                },
                errorMessage -> {
                    onError.accept(errorMessage);
                }
        );
    }

    public void getBorrowedToolsCount(CountCallback callback) {
        mDatabase.child("tools").orderByChild("status").equalTo("Dipinjam")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callback.onResult((int) snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onResult(0);
                    }
                });
    }

    public void getBrokenToolsCount(CountCallback callback) {
        mDatabase.child("tools").orderByChild("toolStatus").equalTo("Rusak")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        callback.onResult((int) snapshot.getChildrenCount());
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        callback.onResult(0);
                    }
                });
    }
    public interface CountCallback {
        void onResult(int count);
    }


}
