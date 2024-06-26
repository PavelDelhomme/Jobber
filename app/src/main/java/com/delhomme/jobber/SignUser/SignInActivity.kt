package com.delhomme.jobber.SignUser

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.delhomme.jobber.MainActivity
import com.delhomme.jobber.R

class SignInActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_in)

        val emailField = findViewById<EditText>(R.id.signup_email)
        val passwordField = findViewById<EditText>(R.id.signup_password)
        val confirmPasswordField = findViewById<EditText>(R.id.confirm_password)
        val signUpButton = findViewById<Button>(R.id.signupButton)
        val backButton = findViewById<Button>(R.id.backButton)

        signUpButton.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()
            val confirmPassword = confirmPasswordField.text.toString().trim()

            if (password == confirmPassword) {
                signUpUser(email, password)
            } else {
                Toast.makeText(this, "Passwords do not match!", Toast.LENGTH_SHORT).show()
            }
        }

        backButton.setOnClickListener {
            finish()
        }
    }

    private fun signUpUser(email: String, password: String) {
        // Mémoriser les données utilisateur en utilisant SharedPreferences
        saveUserData(email, password)
        Toast.makeText(this, "Inscription réussie!", Toast.LENGTH_SHORT).show()
        startActivity(Intent(this, MainActivity::class.java))
    }

    private fun saveUserData(email: String, password: String) {
        val sharedPreferences = getSharedPreferences("user_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.putString("email", email)
        editor.putString("password", password)
        editor.apply()
    }
}

