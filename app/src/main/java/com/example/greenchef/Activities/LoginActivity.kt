package com.example.greenchef.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.greenchef.R
import com.example.greenchef.ViewModels.AuthViewModel

class LoginActivity : AppCompatActivity() {

    private lateinit var authViewModel: AuthViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        authViewModel = ViewModelProvider(this).get(AuthViewModel::class.java)

        // Initialize UI components
        val usernameEditText: EditText = findViewById(R.id.et_username)
        val passwordEditText: EditText = findViewById(R.id.et_password)
        val loginButton: Button = findViewById(R.id.btn_login)
        val signUpTextView: TextView = findViewById(R.id.tv_signup)

        // Set onClickListener for the login button
        loginButton.setOnClickListener {
            val email = usernameEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Call the signIn method in AuthViewModel
                authViewModel.signIn(email, password, this)
            }
        }

        // Set onClickListener for the sign-up TextView
        signUpTextView.setOnClickListener {
            // Navigate to the SignUpActivity or any other activity for sign-up
            startActivity(Intent(this, SignupActivity::class.java))
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