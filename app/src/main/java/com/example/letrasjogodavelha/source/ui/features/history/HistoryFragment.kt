package com.example.letrasjogodavelha.source.ui.features.history

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.letrasjogodavelha.databinding.HistoryFragmentBinding
import com.example.letrasjogodavelha.source.domain.models.Match
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class HistoryFragment: Fragment() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: HistoryAdapter
    private lateinit var binding: HistoryFragmentBinding
    val list = listOf<Match>(Match("LUCAO", "MATHEUS", "HOJE"))


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HistoryFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecycler()

    }

    private fun setupRecycler() {
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        adapter = HistoryAdapter(getMatches().reversed())
        binding.recycler.adapter = adapter
    }

    private fun getMatches(): List<Match> {
        val sharedPreferences = activity?.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        val savedListJson = sharedPreferences?.getString("HISTORY", null)
        val listType = object: TypeToken<MutableList<Match>>() {}.type
        savedListJson?.let {
            return Gson().fromJson(it, listType)
        }
        return listOf()
    }
}