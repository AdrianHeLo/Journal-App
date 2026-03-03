package com.adrianhelo.journalapp.presentation.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import com.adrianhelo.journalapp.R
import com.adrianhelo.journalapp.data.JournalModel
import com.adrianhelo.journalapp.data.JournalUser
import com.adrianhelo.journalapp.databinding.ActivityAddJournalBinding
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Date

class AddJournalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddJournalBinding

    private lateinit var auth: FirebaseAuth
    private var user: FirebaseUser? = null
    private lateinit var storage: StorageReference
    private var imageUri: Uri? = null

    private var db = FirebaseFirestore.getInstance()
    private var collection: CollectionReference = db.collection("Journal")
    private var currentUser: String = ""
    private var currentUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_journal)

        storage = FirebaseStorage.getInstance().getReference()
        auth = Firebase.auth

        binding.apply {
            progressCircularActivityAddJournal.visibility = View.INVISIBLE

            if (JournalUser.instance != null){
                var journal: JournalUser = JournalUser.instance!!
                journal.username = auth.currentUser?.displayName.toString()
                journal.userId = auth.currentUser?.uid.toString()
                postUsernameActivityAddJournal.text = currentUser
            }

            postCameraActivityAddJournal.setOnClickListener {
                getAction.launch("image/*")
            }

            saveButtonActivityAddJournal.setOnClickListener {
                saveJournal()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        user = auth.currentUser
    }

    override fun onStop() {
        super.onStop()
        if (auth != null){

        }
    }

    private fun saveJournal(){
        val title = binding.postUsernameInputActivityAddJournal.text.toString().trim()
        val thoughts = binding.postThoughtsInputActivityAddJournal.text.toString().trim()

        if (title.isNotEmpty() && thoughts.isNotEmpty() && imageUri != null) {
            binding.progressCircularActivityAddJournal.visibility = View.VISIBLE

            val filePath = storage.child("journal_images").child("image_${Timestamp.now().seconds}")

            filePath.putFile(imageUri!!)
                .continueWithTask { task ->
                    if (!task.isSuccessful) {
                        task.exception?.let { throw it }
                    }
                    filePath.downloadUrl
                }
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val downloadUri = task.result.toString()

                        val journal = JournalModel(
                            title,
                            auth.currentUser?.displayName.toString(),
                            auth.currentUser?.uid.toString(),
                            downloadUri,
                            thoughts,
                            Timestamp(Date())
                        )

                        collection.add(journal)
                            .addOnSuccessListener {
                                binding.progressCircularActivityAddJournal.visibility = View.INVISIBLE
                                Log.i("FirestoreSuccess", "Saved in Firestore")
                                startActivity(Intent(this, JournalActivity::class.java))
                                finish()
                            }
                            .addOnFailureListener { e ->
                                binding.progressCircularActivityAddJournal.visibility = View.INVISIBLE
                                Log.e("FirestoreError", "Error saving", e)
                            }
                    }
                }
                .addOnFailureListener {
                    binding.progressCircularActivityAddJournal.visibility = View.INVISIBLE
                    Log.e("FirestoreError", "Error to upload the image")
                    Toast.makeText(this, "Error to upload the image", Toast.LENGTH_SHORT).show()
                }
        } else {
            Log.e("FirestoreError", "Complete all fields")
            Toast.makeText(this, "Complete all fields", Toast.LENGTH_SHORT).show()
        }
    }

    private val getAction = registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        if (uri != null) {
            imageUri = uri
            binding.imageActivityAddJournal.setImageURI(imageUri)
        }
    }

}