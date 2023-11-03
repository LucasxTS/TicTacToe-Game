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

    fun onCreate() {
        startTimer()
    }

    private fun startTimer() {
        val mainHandler = Handler(Looper.getMainLooper())
        mainHandler.post(object: Runnable {
            override fun run() {
                seconds.postValue(seconds.value?.plus(1) ?: 0)
                mainHandler.postDelayed(this, 1000)
            }
        })
    }

    fun didSelect(index: Int): Tile {
        firstPlayerTurn = !firstPlayerTurn
        val tile = if(firstPlayerTurn) Tile.X else Tile.O
        tileList[index] = tile
        return tile
    }

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

    fun checkDraw(): Boolean {
        return !tileList.contains(Tile.EMPTY) && !checkWin()
    }

    fun getRandomEmptyIndex(): Int? {
        if (checkDraw() || checkWin()){
            return null
        }
        val emptyIndexes = (0 until tileList.size).filter { tileList[it] == Tile.EMPTY }
        val randomEmptyIndex = emptyIndexes.random()
        return randomEmptyIndex
    }
}