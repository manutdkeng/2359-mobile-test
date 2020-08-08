package com.zack.android.test.cardgame.viewmodel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.*
import com.zack.android.test.cardgame.data.Card
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class CardGameViewModel(private val pairSize: Int) : ViewModel() {
    private var cards: MutableList<Card> = mutableListOf()
    private var flippedPosition: MutableList<Int> = mutableListOf()

    private val _cardsLiveData = MutableLiveData<MutableList<Card>>()
    val cardsLiveData: LiveData<MutableList<Card>>
        get() = _cardsLiveData

    private val _flipCardLiveData = MutableLiveData<Int>()
    val flipCardLiveData: LiveData<Int>
        get() = _flipCardLiveData

    private val _closeAllCardsLiveData = MutableLiveData<MutableList<Int>?>()
    val closeAllCardsLiveData: LiveData<MutableList<Int>?>
        get() = _closeAllCardsLiveData

    private val _stepsLiveData = MutableLiveData<Int>()
    val stepsLiveData: LiveData<Int>
        get() = _stepsLiveData

    private val _showCompletedDialogLiveData = MutableLiveData<Boolean>()
    val showCompletedDialogLiveData: LiveData<Boolean>
        get() = _showCompletedDialogLiveData

    init {
        generateCards()
    }

    fun dialogShown() {
        _showCompletedDialogLiveData.value = false
    }

    fun closedAllCards() {
        _closeAllCardsLiveData.value = null
    }

    fun flipCard(position: Int) {
        if (!flippedPosition.contains(position)) {
            val card = cards[position]
            card.revealed = true
            _flipCardLiveData.value = position
            increaseSteps()

            if (flippedPosition.size % 2 == 0) {
                flippedPosition.add(position)
            } else {
                val prevPos = flippedPosition.last()
                flippedPosition.add(position)
                compareCards(prevPos, position)
            }
        }
    }

    private fun increaseSteps() {
        var steps: Int = _stepsLiveData.value ?: 0
        _stepsLiveData.value = ++steps
    }

    private fun compareCards(firstPos: Int, secondPos: Int) {
        val first = cards[firstPos]
        val second = cards[secondPos]

        if (first.value != second.value) {
            flipBack(firstPos, secondPos)
        } else {
            // all cards flipped
            if (flippedPosition.size == cards.size) {
                _showCompletedDialogLiveData.value = true
            }
        }
    }

    private fun flipBack(firstPos: Int, secondPos: Int) {
        viewModelScope.launch {
            delay(1_000)
            val first = cards[firstPos]
            first.revealed = false
            _flipCardLiveData.value = firstPos
            flippedPosition.remove(firstPos)

            val second = cards[secondPos]
            second.revealed = false
            _flipCardLiveData.value = secondPos
            flippedPosition.remove(secondPos)
        }
    }

    fun cardFlipped() {
        _flipCardLiveData.value = -1
    }


    fun reset() {
        closeAllRevealedCards()
        _stepsLiveData.value = 0
        flippedPosition.clear()
        viewModelScope.launch {
            // delay generate new cards for closing animation
            delay(600)
            generateCards()
        }
    }

    private fun closeAllRevealedCards() {
        val position = mutableListOf<Int>()
        position.addAll(flippedPosition)
        _closeAllCardsLiveData.value = position
    }

    private fun generateCards() {
        cards.clear()
        val cardSize = pairSize * 2
        // generate pairSize of random unique number
        val numSet = mutableSetOf<Int>()
        while (numSet.size < pairSize) {
            numSet.add((1..100).random())
        }

        val appearedNum = mutableSetOf<Int>()
        for (i in 0 until cardSize) {
            val num = numSet.random()
            cards.add(Card(num))
            // if random number appeared twice remove it from numSet
            if (appearedNum.contains(num)) {
                numSet.remove(num)
            } else {
                appearedNum.add(num)
            }
        }

        _cardsLiveData.value = cards
    }

    @VisibleForTesting(otherwise = VisibleForTesting.NONE)
    fun setSampleCards(cards: List<Card>) {
        this.cards.clear()
        this.cards.addAll(cards)
    }
}

class CardGameViewModelFactory(private val pairSize: Int) : ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(CardGameViewModel::class.java)) {
            return CardGameViewModel(pairSize) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}