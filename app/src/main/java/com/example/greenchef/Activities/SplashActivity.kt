package com.example.greenchef.Activities

import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.WindowInsets
import android.view.WindowManager
import androidx.activity.viewModels
import com.example.greenchef.AppConfiguration
import com.example.greenchef.R
import com.example.greenchef.ViewModels.AuthViewModel

class SplashActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        Handler(Looper.getMainLooper()).postDelayed({
            checkUserStatus()
        }, 2500)
    }
    private fun checkUserStatus() {
        authViewModel.isUserSignedIn.observe(this) { isSignedIn ->
            if (isSignedIn) {
                // User is signed in, navigate to MainActivity
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                // User is not signed in, navigate to LoginActivity
                startActivity(Intent(this, LoginActivity::class.java))
            }
            finish()
        }
    }
}