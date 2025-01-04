package com.example.greenchef.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.greenchef.Objects.GlobalVariables
import com.example.greenchef.R
import com.example.greenchef.ViewModels.AuthViewModel
import com.example.greenchef.ViewModels.UserViewModel

class SignupActivity : AppCompatActivity() {

    private val authViewModel: AuthViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)

        val usernameEditText: EditText = findViewById(R.id.et_username_signup)
        val passwordEditText: EditText = findViewById(R.id.et_password_signup)
        val emailEditText: EditText = findViewById(R.id.et_email_signup)
        val signupButton: Button = findViewById(R.id.btn_signup)
        val signInTextView: TextView = findViewById(R.id.tv_signin)

        signupButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val username = usernameEditText.text.toString().trim()


            if (email.isNotEmpty() && password.isNotEmpty() && username.isNotEmpty()) {
                authViewModel.signUp(email, password, this)

            }
        }

        signInTextView.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        authViewModel.isUserSignedIn.observe(this) { isSignedIn ->
            if (isSignedIn) {

                val username = usernameEditText.text.toString().trim()
                val userid = authViewModel.currentUser.value!!.uid
                val userViewModel = UserViewModel(userid)
                userViewModel.userLiveData.observe(this) { userdata ->
                    userViewModel.updateUserName(username)
                    GlobalVariables.currentUser = userdata
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                }
            }
        }
    }
}