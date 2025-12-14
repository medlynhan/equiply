package com.example.equiply.database;

import android.content.Context;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.equiply.helper.CloudinaryHelper;
import com.example.equiply.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.function.Consumer;

public class UserDA {
    private final DatabaseReference mDatabase;

    public UserDA() {
        this.mDatabase = FirebaseDatabase.getInstance().getReference();
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
}
