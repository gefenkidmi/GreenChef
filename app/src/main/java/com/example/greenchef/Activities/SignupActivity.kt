package com.example.greenchef.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.greenchef.R
import com.example.greenchef.ViewModels.AuthViewModel

class SignupActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        // Initialize UI components
        val usernameEditText: EditText = findViewById(R.id.et_username_signup)
        val passwordEditText: EditText = findViewById(R.id.et_password_signup)
        val emailEditText: EditText = findViewById(R.id.et_email_signup)
        val signupButton: Button = findViewById(R.id.btn_signup)
        val signInTextView: TextView = findViewById(R.id.tv_signin)

        // Set onClickListener for the sign-up button
        signupButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                // Call the signUp method in AuthViewModel
                authViewModel.signUp(email, password, username, this)
            }
        }

        // Set onClickListener for the sign-in TextView
        signInTextView.setOnClickListener {
            // Navigate to the LoginActivity or any other activity for sign-in
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        // Observe the isUserSignedIn LiveData to determine the authentication state
        authViewModel.isUserSignedIn.observe(this) { isSignedIn ->
            if (isSignedIn) {
                // User is signed in, navigate to the MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        }
    }
}