package com.example.jefiro.barber.service;


import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

public class App extends Application {
    private static FirebaseAuth mAuth;
    private static FirebaseFirestore db;

    @Override
    public void onCreate() {
        super.onCreate();

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }


    public static FirebaseAuth getmAuth() {
        return mAuth;
    }

    public static FirebaseFirestore getDb() {
        return db;
    }
}