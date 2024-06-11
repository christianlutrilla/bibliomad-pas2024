package com.clamor.bibliomad;

import android.app.Application;
import android.util.Log;

import com.google.firebase.FirebaseApp;

public class BibliomadApp extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseApp.initializeApp(this);
        Log.d("BibliomadApp", "Firebase initialized in Application class");
    }
}
