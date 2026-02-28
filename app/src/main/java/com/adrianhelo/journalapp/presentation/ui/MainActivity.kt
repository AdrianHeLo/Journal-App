package com.adrianhelo.journalapp.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adrianhelo.journalapp.R
import com.adrianhelo.journalapp.databinding.ActivityMainBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.loginButtonActivityMain.setOnClickListener {
            var email: String = binding.emailLoginActivityMain.text.toString().trim()
            var password: String = binding.passwordLoginActivityMain.text.toString().trim()
           loginUser(email, password)
        }
        binding.createAccountButtonActivityMain.setOnClickListener {
           var intent = Intent(this, SignUpActivity::class.java)
           startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if (currentUser != null) {
            updateUI()
        }
    }

    private fun loginUser(email: String, password: String) {
        val user = Firebase.auth.currentUser
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("TAG", "signInWithEmail:success")
                    val user = auth.currentUser
                    updateUI()
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("TAG", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        baseContext,
                        "Authentication failed.",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            }
    }

    private fun updateUI() {
        var intent: Intent = Intent(this, JournalActivity::class.java)
        startActivity(intent)
    }
}