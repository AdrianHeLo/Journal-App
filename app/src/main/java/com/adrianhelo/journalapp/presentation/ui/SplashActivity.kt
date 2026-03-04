package com.adrianhelo.journalapp.presentation.ui

import android.animation.Animator
import android.app.FragmentManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.adrianhelo.journalapp.R
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        supportActionBar?.hide()

        auth = Firebase.auth

        val animationView = findViewById<LottieAnimationView>(R.id.lottie_layer)
        animationView.playAnimation()

        lifecycleScope.launch {
            delay(4000)
            val currentUser = auth.currentUser
            if (currentUser != null){
                var intent: Intent = Intent(this@SplashActivity, JournalActivity::class.java)
                startActivity(intent)
            }else{
                var intent: Intent = Intent(this@SplashActivity, MainActivity::class.java)
                startActivity(intent)
            }
            finish()
        }
    }
}