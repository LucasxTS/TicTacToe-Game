package com.example.letrasjogodavelha.source.ui.features.game


import android.content.Context
import android.graphics.PorterDuff
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.letrasjogodavelha.R
import com.example.letrasjogodavelha.databinding.GameFragmentBinding
import com.example.letrasjogodavelha.source.domain.models.Match
import com.example.letrasjogodavelha.source.domain.models.Tile
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.util.Locale

class GameFragment: Fragment() {

    private lateinit var binding: GameFragmentBinding
    private lateinit var viewModel: GameViewModel

    private var isSinglePlayer: Boolean = false
    private var firstPlayer: String = ""
    private var secondPlayer: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]
        isSinglePlayer = arguments?.getBoolean("isSinglePlayer") ?: false
        firstPlayer = arguments?.getString("firstPlayer") ?: ""
        val secondPlayerArgument = arguments?.getString("secondPlayer") ?: ""
        secondPlayer = if(secondPlayerArgument.isEmpty()) "Bot" else secondPlayerArgument
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
        bindLiveData()
        viewModel.onCreate()
    }

    private fun bindLiveData() {
        viewModel.seconds.observe(viewLifecycleOwner) {
            binding.stopwatch.text = formattedDate(it)
        }
    }

    private fun formattedDate(seconds: Int): String {
        val hours: Int = seconds / 3600
        val minutes: Int = seconds % 3600 / 60
        val secs: Int = seconds % 60
        val text = String.format(
            Locale.getDefault(),
            "%d:%02d:%02d",
            hours, minutes, secs)
        return text
    }

    private fun bindUI() {
        val bindings = listOf(binding.t1, binding.t2, binding.t3, binding.t4, binding.t5, binding.t6, binding.t7, binding.t8, binding.t9)
        bindings.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                didSelectAt(index, imageView)
            }
        }

        binding.buttonReset.setOnClickListener {
            resetAllTiles(bindings)
        }

        binding.buttonBack.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.playerName.text = firstPlayer
    }

    private fun didSelectAt(index: Int, imageView: ImageView) {
        if (imageView.alpha == 1f) {
            return
        }
        applyTile(index, imageView)
        playForComputerIfNeeded()
    }

    private fun resetAllTiles(imageViewList: List<ImageView>) {
        imageViewList.forEach {
            it.alpha = 0f
            it.isEnabled = true
        }
        binding.buttonContainer.visibility = View.GONE
        viewModel.resetTileList()
        binding.playerName.text = firstPlayer
        binding.playerName.setTextColor(resources.getColor(R.color.blue, null))
        binding.description.text = "Vez do jogador"
        binding.playerName.visibility = View.VISIBLE
    }

    private fun applyTile(index: Int, imageView: ImageView) {
        val tile = viewModel.didSelect(index)
        val resource = when (tile) {
            Tile.X -> R.drawable.close
            Tile.O -> R.drawable.baseline_circle_24
            Tile.EMPTY -> R.drawable.baseline_circle_24
        }
        val imageTint = when (tile) {
            Tile.X -> R.color.red
            Tile.O -> R.color.blue
            Tile.EMPTY -> R.color.blue
        }
        binding.playerName.text = if(tile == Tile.X) firstPlayer else secondPlayer
        binding.playerName.setTextColor(resources.getColor(if(tile == Tile.X) R.color.blue else R.color.red, null))
        imageView.setColorFilter(resources.getColor(imageTint, null), PorterDuff.Mode.MULTIPLY)
        imageView.setImageResource(resource)
        imageView.alpha = 1f
        checkStatus(tile)
    }

    private fun checkStatus(lastTile: Tile) {
        if (viewModel.checkWin()) {
            binding.buttonContainer.visibility = View.VISIBLE
            didWin(lastTile)
            saveMatch(lastTile, true)
        }

        if (viewModel.checkDraw()) {
            binding.buttonContainer.visibility = View.VISIBLE
            didDraw()
            saveMatch(lastTile, false)
        }
    }

    private fun didWin(lastTile: Tile) {
        val color = if(lastTile == Tile.O) R.color.blue else R.color.red
        val winnerName = if(lastTile == Tile.O) firstPlayer else secondPlayer
        binding.description.text = "Vencedor 🏆"
        binding.playerName.text = winnerName
        binding.playerName.setTextColor(resources.getColor(color, null))
        val bindings = listOf(binding.t1, binding.t2, binding.t3, binding.t4, binding.t5, binding.t6, binding.t7, binding.t8, binding.t9)
        bindings.forEach {
            it.isEnabled = false
        }
    }

    private fun saveMatch(lastTile: Tile, win: Boolean) {
        val match = createMatch(lastTile, win)
        val allMatches = getMatches() + match
        val stringAllMatches = Gson().toJson(allMatches)
        val sharedpreferences = activity?.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        val editor = sharedpreferences?.edit()
        editor?.apply {
            putString("HISTORY", stringAllMatches)
        }?.apply()
    }

    private fun getMatches(): MutableList<Match> {
        val sharedPreferences = activity?.getSharedPreferences("sharedPreferences", Context.MODE_PRIVATE)
        val savedListJson = sharedPreferences?.getString("HISTORY", null)
        val listType = object: TypeToken<MutableList<Match>>() {}.type
        savedListJson?.let {
            return Gson().fromJson(it, listType)
        }
        return mutableListOf()
    }

    private fun createMatch(lastTile: Tile, win: Boolean): Match {
        val winner = if(lastTile == Tile.O) firstPlayer else secondPlayer
        val seconds = viewModel.seconds.value ?: 0
        val winnerMessage = "$winner venceu! (${formattedDate(seconds)})"
        val drawMessage = "Empate! (${formattedDate(seconds)})"
        val match = Match(firstPlayer, secondPlayer, if(win) winnerMessage else drawMessage)
        return match
    }

    private fun didDraw() {
        binding.description.text = "Empate!"
        binding.playerName.visibility = View.GONE
    }

    private fun playForComputerIfNeeded() {
        if (isSinglePlayer) {
            viewModel.getRandomEmptyIndex()?.let {
                applyTile(it, getImageViewForIndex(it))
            }
        }
    }

    private fun getImageViewForIndex(index: Int): ImageView {
        return when (index) {
            0 -> binding.t1
            1 -> binding.t2
            2 -> binding.t3
            3 -> binding.t4
            4 -> binding.t5
            5 -> binding.t6
            6 -> binding.t7
            7 -> binding.t8
            8 -> binding.t9
            else -> binding.t1
        }
    }
}