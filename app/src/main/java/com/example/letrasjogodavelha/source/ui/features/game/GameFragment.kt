package com.example.letrasjogodavelha.source.ui.features.game


import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.letrasjogodavelha.R
import com.example.letrasjogodavelha.databinding.GameFragmentBinding
import com.example.letrasjogodavelha.source.domain.models.Tile
import java.util.Locale

class GameFragment: Fragment() {

    private lateinit var binding: GameFragmentBinding
    private lateinit var viewModel: GameViewModel

    private var isSinglePlayer: Boolean = true
    private var playsFirst: Boolean = true

    // ONDE INICIALIZAMOS VARIAVEIS OU PEGAR ALGO QUE NAO ENVOLVA VIEWS
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this)[GameViewModel::class.java]
        isSinglePlayer = arguments?.getBoolean("isSinglePlayer") ?: true
        playsFirst = arguments?.getBoolean("playsFirst") ?: true
    }
    // CRIANDO AS VIEWS E SETANDO O LAYOUT
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = GameFragmentBinding.inflate(inflater, container, false)
        return binding.root
    }
    //VIEW JA CRIADA E CHAMANDO FUNÇÕES PRO FUNCIONAMENTO DO APP
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        bindUI()
        computerPlaysFirst()
        bindLiveData()
        viewModel.onCreate()
    }
    // BINDANDO O TIMER DA VIEWMODEL NO TEXTVIEW
    private fun bindLiveData() {
        viewModel.seconds.observe(viewLifecycleOwner) {
            val hours: Int = it / 3600
            val minutes: Int = it % 3600 / 60
            val secs: Int = it % 60
            val text = String.format(
                Locale.getDefault(),
                "%d:%02d:%02d",
                hours, minutes, secs)
            binding.stopwatch.text = text
        }
    }
    // VERIFICANDO QUEM IRA JOGAR PRIMEIRO CASO O USUARIO ESCOLHA O CIRCULO
    private fun computerPlaysFirst() {
        if (playsFirst) {
            return
        }
        playForComputerIfNeeded()
    }
    // CRIANDO UMA LISTA COM AS IMAGEVIEWS E USANDO UM FOREACH PARA PEGAR O CAMPO SELECIONADO
    private fun bindUI() {
        val bindings = listOf(binding.t1, binding.t2, binding.t3, binding.t4, binding.t5, binding.t6, binding.t7, binding.t8, binding.t9)
        bindings.forEachIndexed { index, imageView ->
            imageView.setOnClickListener {
                didSelectAt(index, imageView)
            }
        }
    }
    // APLICANDO O TILE NA IMAGEVIEW SELECIONADO PELA BINDUI
    private fun didSelectAt(index: Int, imageView: ImageView) {
        if (imageView.alpha == 1f) {
            return
        }
        applyTile(index, imageView)
        playForComputerIfNeeded()
    }
    // COLOCANDO O TILE DO USUARIO NA LISTA DA VIEWMODEL COM O INDEX E APLICANDO O TILE NA VIEW
    private fun applyTile(index: Int, imageView: ImageView) {
        val tile = viewModel.didSelect(index)
        val resource = when (tile) {
            Tile.X -> R.drawable.close
            Tile.O -> R.drawable.baseline_circle_24
            Tile.EMPTY -> R.drawable.baseline_circle_24
        }
        imageView.setImageResource(resource)
        imageView.alpha = 1f
        checkStatus(tile)
    }
    // VERIFICANDO SE O JOGO TEVE UM VENCEDOR OU FOI EMPATE
    private fun checkStatus(lastTile: Tile) {
        if (viewModel.checkWin()) {
            openDialogWith(getString(R.string.win), "${lastTile.toString().uppercase()} ${getString(R.string.win_the_match)}")
        }

        if (viewModel.checkDraw()) {

            openDialogWith(getString(R.string.draw), getString(R.string.the_match_tie))
        }
    }
    // FAZENDO O COMPUTADOR SELECIONAR UMA VIEW PARA PREENCHER
    private fun playForComputerIfNeeded() {
        if (isSinglePlayer) {
            viewModel.getRandomEmptyIndex()?.let {
                applyTile(it, getImageViewForIndex(it))
            }
        }
    }
    // PEGANDO A VIEW POR INDEX
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
    // CAIXA DE DIALOGO PARA APRESENTAR O GANHADOR OU EMPATE
    private fun openDialogWith(title: String, message: String) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK") { _, _ ->
                findNavController().popBackStack()
            }

        val dialog: AlertDialog = builder.create()
        dialog.show()
    }
}