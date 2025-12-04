package com.example.equiply.helper;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

import com.example.equiply.admin_activity.AdminDashboardActivity;
import com.example.equiply.student_activity.HomeDashboardActivity;
import com.example.equiply.LoginActivity;
import com.example.equiply.MainActivity;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseUser;

public class AuthFirebase {
    private final FirebaseAuth mAuth;
    private final RealtimeDatabaseFirebase db;

    public interface AuthCallback {
        void onSuccess();
        void onFailure(String message);
    }

    public AuthFirebase(Context context) {
        this.mAuth = FirebaseAuth.getInstance();
        this.db = new RealtimeDatabaseFirebase(context);
    }

    public void register(Context context,String name, String nim,String email, String password, AuthCallback callback) {
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();

                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        String id = firebaseUser.getUid();
                        db.addNewUser(context,id,name,nim, email);
                        Intent intent = new Intent(context, LoginActivity.class);
                        context.startActivity(intent);
                    } else {
                        callback.onFailure(task.getException().getMessage());
                    }
                });
    }

    public void login(Context context, String email, String password, AuthCallback callback) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        callback.onSuccess();

                        Toast.makeText(context, "Login Success.",
                                Toast.LENGTH_SHORT).show();

                        if (email.equals("admin@gmail.com")) {
                            Intent intent = new Intent(context, AdminDashboardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        } else {
                            Intent intent = new Intent(context, HomeDashboardActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            context.startActivity(intent);
                        }
                    } else {
                        String errorMessage = "Login failed.";

                        Exception exception = task.getException();
                        if (exception != null) {
                            try {
                                throw exception;
                            } catch (FirebaseAuthInvalidUserException e) {
                                errorMessage = "Account does not exist.";
                            } catch (FirebaseAuthInvalidCredentialsException e) {
                                errorMessage = "Wrong email or password.";
                            } catch (FirebaseNetworkException e) {
                                errorMessage = "Please check your internet connection.";
                            } catch (Exception e) {
                                errorMessage = "Authentication failed: " + e.getMessage();
                            }
                        }

                        callback.onFailure(errorMessage);
                    }
                });
    }

    public void logout(Context context){
        mAuth.signOut();
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP |  Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);
    }

    public FirebaseUser getTheCurrentUser(){
        return mAuth.getCurrentUser();
    }





}
