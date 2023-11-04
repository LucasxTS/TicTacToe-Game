package com.example.letrasjogodavelha.source.ui.features.home

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.letrasjogodavelha.R
import com.example.letrasjogodavelha.databinding.HomeFragmentBinding

class HomeFragment: Fragment() {

    private var isSinglePlayer: Boolean = false
    private lateinit var binding: HomeFragmentBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
        setState(isSinglePlayer)
    }

    override fun onResume() {
        super.onResume()
        binding.playerEdittext.setText(getPlayerName())
    }

    private fun bindUI() {
        binding.buttonGoToGame.setOnClickListener {
            navigateToGame()
        }

        binding.buttonGoToHistory.setOnClickListener {
            navigateWithAction(R.id.action_go_to_history, null)
        }

        binding.vsBot.setOnClickListener {
            setState(true)
        }

        binding.vsPlayer.setOnClickListener {
            setState(false)
        }
    }

    private fun navigateToGame() {
        savePlayerName()
        if (validateNavigateToGame()) {
            navigateWithAction(R.id.action_go_to_game, gameBundle())
        }
    }

    private fun getPlayerName(): String {
        val sharedPreferences = activity?.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        val savedName = sharedPreferences?.getString("firstPlayer", null)
        return savedName ?: ""
    }

    private fun savePlayerName() {
        val sharedPreferences = activity?.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedPreferences?.edit()
        editor?.apply {
            putString("firstPlayer", binding.playerEdittext.text.toString())
        }?.apply()
    }

    private fun validateNavigateToGame(): Boolean {
        if (isSinglePlayer) {
            return binding.playerEdittext.text.isNotEmpty()
        }
        return binding.playerEdittext.text.isNotEmpty() && binding.botEdittext.text.isNotEmpty()
    }

    private fun setState(isSinglePlayer: Boolean) {
        binding.botEdittext.text.clear()
        binding.botEdittext.visibility = if(isSinglePlayer) View.GONE else View.VISIBLE
        binding.buttonToggleGroup.check(if(isSinglePlayer) R.id.vs_bot else R.id.vs_player)
        this.isSinglePlayer = isSinglePlayer
    }

    private fun navigateWithAction(action: Int, bundle: Bundle?) {
        findNavController().navigate(action, bundle)
    }

    private fun gameBundle(): Bundle {
        return bundleOf(
            "firstPlayer" to binding.playerEdittext.text.toString(),
            "secondPlayer" to binding.botEdittext.text.toString(),
            "isSinglePlayer" to isSinglePlayer)
    }
}