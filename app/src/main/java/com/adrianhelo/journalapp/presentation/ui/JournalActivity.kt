package com.adrianhelo.journalapp.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.LinearLayoutManager
import com.adrianhelo.journalapp.R
import com.adrianhelo.journalapp.data.JournalModel
import com.adrianhelo.journalapp.data.JournalUser
import com.adrianhelo.journalapp.databinding.ActivityJournalBinding
import com.adrianhelo.journalapp.presentation.adapter.JournalAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference

class JournalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityJournalBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var user: FirebaseUser
    lateinit var storageReference: StorageReference
    private var db = FirebaseFirestore.getInstance()
    private var collection: CollectionReference = db.collection("Journal")

    private lateinit var adapter: JournalAdapter
    private lateinit var journalList: MutableList<JournalModel>
    private lateinit var noPostTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = DataBindingUtil.setContentView(this, R.layout.activity_journal)

        auth = Firebase.auth
        user = auth.currentUser!!

        binding.recyclerContainerActivityJournal.setHasFixedSize(true)
        binding.recyclerContainerActivityJournal.layoutManager = LinearLayoutManager(this)
        journalList = arrayListOf<JournalModel>()
    }

    override fun onStart() {
        super.onStart()
        collection.whereEqualTo("userId", JournalUser.instance?.userId)
            .get()
            .addOnSuccessListener { it ->
                if (!it.isEmpty){
                it.forEach{
                    var journal = it.toObject(JournalModel::class.java)
                    journalList.add(journal)
                }
                binding.message.visibility = View.GONE
                adapter = JournalAdapter(this, journalList)
                binding.recyclerContainerActivityJournal.adapter = adapter
                adapter.notifyDataSetChanged()
            }else {
                binding.message.visibility = View.VISIBLE
            }
        }.addOnFailureListener {
            Toast.makeText(this, "Opps! Something went wrong!", Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.journal_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.add_menu_item -> if (user != null && auth != null){
                val intent = Intent(this, AddJournalActivity::class.java)
                startActivity(intent)
            }
            R.id.logout_item -> if (user != null && auth != null){
                auth.signOut()
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
            }
        }
        return super.onOptionsItemSelected(item)
    }

}