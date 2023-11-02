package com.example.letrasjogodavelha.source.ui.features.home

import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.bundleOf
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.letrasjogodavelha.R
import com.example.letrasjogodavelha.databinding.HomeFragmentBinding
import com.example.letrasjogodavelha.source.domain.models.Tile

class HomeFragment: Fragment() {

    private lateinit var binding: HomeFragmentBinding
    private var playsFirst: Boolean = true
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = HomeFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
        setPlayerTile(if(playsFirst) Tile.X else Tile.O)
    }

    private fun bindUI() {
        binding.buttonAI.setOnClickListener {
            navigateToGame(true)
        }

        binding.buttonPlayer.setOnClickListener {
            navigateToGame(false)
        }

        binding.closeIcon.setOnClickListener {
            setPlayerTile(Tile.X)
        }

        binding.circleIcon.setOnClickListener {
            setPlayerTile(Tile.O)
        }
    }

    private fun navigateToGame(isSinglePlayer: Boolean) {
        val bundle = bundleOf("isSinglePlayer" to isSinglePlayer)
        findNavController().navigate(R.id.action_goToGame, bundle)
    }

    private fun setPlayerTile(tile: Tile) {
        playsFirst = tile == Tile.X
        val mainColor = if (tile == Tile.X) R.color.accent else R.color.background
        val secondaryColor= if(tile == Tile.X) R.color.background else R.color.accent
        binding.closeIcon.setBackgroundColor(resources.getColor(mainColor, null))
        binding.closeIcon.setColorFilter(resources.getColor(secondaryColor, null), PorterDuff.Mode.MULTIPLY)
        binding.circleIcon.setBackgroundColor(resources.getColor(secondaryColor, null))
        binding.circleIcon.setColorFilter(resources.getColor(mainColor, null), PorterDuff.Mode.MULTIPLY)
    }
}