package com.example.greenchef

import android.app.Application
import com.google.firebase.FirebaseApp
import com.google.firebase.appcheck.FirebaseAppCheck
import com.google.firebase.appcheck.playintegrity.PlayIntegrityAppCheckProviderFactory
// For debugging only:
// import com.google.firebase.appcheck.debug.DebugAppCheckProviderFactory

class MyApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1) Initialize Firebase
        FirebaseApp.initializeApp(this)

        // 2) Initialize Firebase App Check
        FirebaseAppCheck.getInstance()
            .installAppCheckProviderFactory(
                PlayIntegrityAppCheckProviderFactory.getInstance()
            )
        // For debugging only – if you want to skip PlayIntegrity in Debug mode:
        /*
        if (BuildConfig.DEBUG) {
            firebaseAppCheck.installAppCheckProviderFactory(
                DebugAppCheckProviderFactory.getInstance()
            )
        }
        */
    }
}

