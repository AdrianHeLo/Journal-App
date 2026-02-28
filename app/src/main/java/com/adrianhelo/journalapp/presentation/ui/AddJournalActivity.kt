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
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.Date

class AddJournalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddJournalBinding

    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    private lateinit var storage: StorageReference
    private lateinit var imageUri: Uri

    private var db = FirebaseFirestore.getInstance()
    private var collection: CollectionReference = db.collection("Journal")
    private var currentUser: String = ""
    private var currentUserId: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_add_journal)

        //user = auth.currentUser!!
        storage = FirebaseStorage.getInstance().getReference()
        auth = FirebaseAuth.getInstance()

        binding.apply {
            progressCircularActivityAddJournal.visibility = View.INVISIBLE

            if (JournalUser.instance != null){
                currentUserId = JournalUser.instance!!.userId.toString()
                currentUser = JournalUser.instance!!.username.toString()
                postUsernameActivityAddJournal.text = currentUser
            }

            postCameraActivityAddJournal.setOnClickListener {
                var i: Intent = Intent(Intent.ACTION_GET_CONTENT)
                i.setType("image/*")
                startActivityForResult(i, 1)
            }

            saveButtonActivityAddJournal.setOnClickListener {
                saveJournal()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        user = auth.currentUser!!
    }

    override fun onStop() {
        super.onStop()
        if (auth != null){

        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?){
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK){
            if (data != null){
                imageUri =data.data!!
                binding.imageActivityAddJournal.setImageURI(imageUri)
            }
        }
    }

    private fun saveJournal(){
        var title: String = binding.postUsernameInputActivityAddJournal.text.toString().trim()
        var thoughts: String = binding.postThoughtsInputActivityAddJournal.text.toString().trim()
        binding.progressCircularActivityAddJournal.visibility = View.VISIBLE

        if (!TextUtils.isEmpty(title) && !TextUtils.isEmpty(thoughts) && imageUri != null){

            val filePath: StorageReference = storage.child("journal").child("_image_"+Timestamp.now().seconds)

            filePath.putFile(imageUri).addOnSuccessListener {
                filePath.downloadUrl.addOnSuccessListener {

                    var uri: String = it.toString()
                    var timestamp: Timestamp = Timestamp(Date())

                    var journal: JournalModel = JournalModel(
                        title,
                        currentUser,
                        currentUserId,
                        uri.toInt(),
                        thoughts,
                        timestamp)

                    Log.i("AJA", journal.toString())

                    collection.add(journal).addOnSuccessListener {
                        binding.progressCircularActivityAddJournal.visibility = View.INVISIBLE
                        Log.e("AJA", collection.toString())
                        var intent: Intent = Intent(this, JournalActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }.addOnFailureListener {
                    binding.progressCircularActivityAddJournal.visibility = View.INVISIBLE
                    Log.e("AJA", "Failed to upload the image")
                    Toast.makeText(this, "Failed to upload the image", Toast.LENGTH_LONG).show()
                }
            }
        }else{
            binding.progressCircularActivityAddJournal.visibility = View.INVISIBLE
            Log.e("AJA", "Fields empties or without image!!!")
            Toast.makeText(this, "Fields empties or without image!!!", Toast.LENGTH_LONG).show()
        }
    }

}