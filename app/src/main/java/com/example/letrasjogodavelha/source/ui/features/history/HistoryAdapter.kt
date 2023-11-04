package com.example.letrasjogodavelha.source.ui.features.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.letrasjogodavelha.databinding.RecyclerCellBinding
import com.example.letrasjogodavelha.source.domain.models.Match

class HistoryAdapter(val list : List<Match>) : RecyclerView.Adapter<HistoryAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: RecyclerCellBinding) :
        RecyclerView.ViewHolder(binding.root) {

            fun bind(history: Match) {
                binding.winner.text = history.winner
                binding.loser.text = history.loser
                binding.date.text = history.description
            }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = RecyclerCellBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val model = list[position]
        holder.bind(model)
    }

    override fun getItemCount() = list.size
}