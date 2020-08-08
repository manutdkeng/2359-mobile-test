package com.zack.android.test.cardgame.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.zack.android.test.cardgame.data.Card

class CardGameViewModel(private val pairSize: Int) : ViewModel() {
    var cards: MutableList<Card> = mutableListOf()

    private val _cardsLiveData = MutableLiveData<List<Card>>()
    val cardsLiveData: LiveData<List<Card>>
        get() = _cardsLiveData

    private val _flipCardLiveData = MutableLiveData<Int>()
    val flipCardLiveData: LiveData<Int>
        get() = _flipCardLiveData

    private val _stepsLiveData = MutableLiveData<Int>()
    val stepsLiveData: LiveData<Int>
        get() = _stepsLiveData

    private val _showCompletedDialogLiveData = MutableLiveData<Boolean>()
    val showCompletedDialogLiveData: LiveData<Boolean>
        get() = _showCompletedDialogLiveData

    fun flipCard(position: Int) {

    }

    fun cardFlipped() {

    }


    fun reset() {

    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun setSampleCards(cards: List<Card>) {
        this.cards.clear()
        this.cards.addAll(cards)
    }
}