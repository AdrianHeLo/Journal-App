package com.adrianhelo.journalapp.presentation.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.adrianhelo.journalapp.data.JournalModel
import com.adrianhelo.journalapp.databinding.JournalItemListBinding

class JournalAdapter(private val context: Context, private val journalList: List<JournalModel>):
    RecyclerView.Adapter<JournalAdapter.ViewHolder>() {

        lateinit var binding: JournalItemListBinding

    inner class ViewHolder(var binding: JournalItemListBinding): RecyclerView.ViewHolder(binding.root){
        fun bind(journal: JournalModel ){
            binding.journal = journal
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = JournalItemListBinding.inflate(LayoutInflater.from(context), parent, false)
        return ViewHolder(binding)
    }

    override fun getItemCount(): Int {
        return journalList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemPosition = journalList[position]
        return holder.bind(itemPosition)
    }
}