package com.adrianhelo.journalapp.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.lifecycleScope
import com.adrianhelo.journalapp.R
import com.adrianhelo.journalapp.data.JournalUser
import com.adrianhelo.journalapp.databinding.ActivityMainBinding
import com.airbnb.lottie.LottieAnimationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var animationView: LottieAnimationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        supportActionBar?.hide()

        binding.lottieLayerUnlockActivityMain.visibility = View.INVISIBLE
        binding.loginActivityMainContainer.visibility = View.VISIBLE

        val monkeyAnimator = binding.lottieLayerLoginActivityMain
        val passwordTextField = binding.passwordTextField
        val passwordEdit = binding.passwordLoginActivityMain

        // Initialize Firebase Auth
        auth = Firebase.auth

        binding.passwordLoginActivityMain.setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus){
                monkeyAnimator.setMinAndMaxFrame(30, 70)
                monkeyAnimator.speed = 2f
                monkeyAnimator.playAnimation()
            }else{
                monkeyAnimator.setMinAndMaxFrame(145, 201)
                monkeyAnimator.speed = 2f
                monkeyAnimator.playAnimation()
            }
        }

        passwordTextField.setEndIconOnClickListener{
            val selection = passwordEdit.selectionEnd
            val isPasswordVisible = passwordEdit.transformationMethod == null

            if (isPasswordVisible){
                monkeyAnimator.setMinAndMaxFrame(100, 145)
                monkeyAnimator.speed = 2f
                passwordEdit.transformationMethod = PasswordTransformationMethod.getInstance()
            }else{
                monkeyAnimator.setMinAndMaxFrame(70, 100)
                passwordEdit.transformationMethod = null
            }
            monkeyAnimator.speed = 2f
            monkeyAnimator.playAnimation()
            passwordEdit.setSelection(selection)
        }

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
                    var journal: JournalUser = JournalUser.instance!!
                    journal.userId = auth.currentUser?.uid
                    journal.username = auth.currentUser?.displayName
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
        binding.loginActivityMainContainer.visibility = View.INVISIBLE
        animationView = findViewById(R.id.lottie_layer_unlock_activity_main)
        animationView.visibility = View.VISIBLE
        animationView.playAnimation()
        lifecycleScope.launch {
            delay(2000)
            var intent: Intent = Intent(this@MainActivity, JournalActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}