package com.example.letrasjogodavelha.source.ui.features.game


import android.os.Handler
import android.os.Looper
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.letrasjogodavelha.source.domain.models.Tile


class GameViewModel: ViewModel() {
    var seconds = MutableLiveData(0)
    private var firstPlayerTurn: Boolean = false

    private var tileList: MutableList<Tile> = mutableListOf(
        Tile.EMPTY,
        Tile.EMPTY,
        Tile.EMPTY,
        Tile.EMPTY,
        Tile.EMPTY,
        Tile.EMPTY,
        Tile.EMPTY,
        Tile.EMPTY,
        Tile.EMPTY
    )

    //CHAMANDO O START TIMER
    fun onCreate() {
        startTimer()
    }

    fun resetTileList() {
        seconds.postValue(0)
        firstPlayerTurn = false
        tileList = mutableListOf(
            Tile.EMPTY,
            Tile.EMPTY,
            Tile.EMPTY,
            Tile.EMPTY,
            Tile.EMPTY,
            Tile.EMPTY,
            Tile.EMPTY,
            Tile.EMPTY,
            Tile.EMPTY
        )
    }
    // INICIANDO O TIMER PARA PASSAR A CADA UM SEGUNDO E COLOCAR O VALOR NO LIVEDATA SEMPRE QUE MUDAR
    private fun startTimer() {
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object: Runnable {
            override fun run() {
                seconds.postValue(seconds.value?.plus(1) ?: 0)
                mainHandler.postDelayed(this, 1000)
            }
        })
    }
    // VERIFICANDO QUAL TILE VAI SER USADO DURANTE O GAME
    fun didSelect(index: Int): Tile {
        firstPlayerTurn = !firstPlayerTurn
        val tile = if(firstPlayerTurn) Tile.O else Tile.X
        tileList[index] = tile
        return tile
    }
    // CHECANDO NA LISTA DE TILES SE O JOGADOR GANHOU DE ACORDO COM AS COMBINAÇÕES
    fun checkWin(): Boolean {
        return (tileList[0] != Tile.EMPTY && tileList[0] == tileList[1] && tileList[1] == tileList[2]) ||
                (tileList[3] != Tile.EMPTY && tileList[3] == tileList[4] && tileList[4] == tileList[5]) ||
                (tileList[6] != Tile.EMPTY && tileList[6] == tileList[7] && tileList[7] == tileList[8]) ||
                (tileList[0] != Tile.EMPTY && tileList[0] == tileList[3] && tileList[3] == tileList[6]) ||
                (tileList[1] != Tile.EMPTY && tileList[1] == tileList[4] && tileList[4] == tileList[7]) ||
                (tileList[2] != Tile.EMPTY && tileList[2] == tileList[5] && tileList[5] == tileList[8]) ||
                (tileList[0] != Tile.EMPTY && tileList[0] == tileList[4] && tileList[4] == tileList[8]) ||
                (tileList[2] != Tile.EMPTY && tileList[2] == tileList[4] && tileList[4] == tileList[6])
    }
    // CHECANDO EMPATE
    fun checkDraw(): Boolean {
        return !tileList.contains(Tile.EMPTY) && !checkWin()
    }
    // FAZENDO O COMPUTADOR PEGAR UM INDEX ALEATORIO ONDE ESTIVER EMPTY NA LISTA
    fun getRandomEmptyIndex(): Int? {
        if (checkDraw() || checkWin()){
            return null
        }
        val emptyIndexes = (0 until tileList.size).filter { tileList[it] == Tile.EMPTY }
        val randomEmptyIndex = emptyIndexes.random()
        return randomEmptyIndex
    }
}