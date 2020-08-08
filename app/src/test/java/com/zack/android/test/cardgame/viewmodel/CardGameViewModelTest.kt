package com.zack.android.test.cardgame.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.zack.android.test.cardgame.LiveDataTestUtil
import com.zack.android.test.cardgame.data.Card
import org.junit.Before
import org.junit.Rule

import org.junit.Test

class CardGameViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: CardGameViewModel
    private val cards = listOf(
        Card(-1),
        Card(-1),
        Card(-2),
        Card(-2)
    )

    @Before
    fun setUp() {
        viewModel = CardGameViewModel(2)
        viewModel.setSampleCards(cards)
    }

    @Test
    fun flipCard_flipOnce() {
        viewModel.flipCard(0)
        assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(0)
        assertThat((LiveDataTestUtil.getValue(viewModel.stepsLiveData))).isEqualTo(1)
    }

    @Test
    fun flipCard_flipSameCardTwice_stepIncreaseOnce() {
        viewModel.flipCard(0)
        assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(0)
        assertThat((LiveDataTestUtil.getValue(viewModel.stepsLiveData))).isEqualTo(1)
        viewModel.cardFlipped() // reset flipCardLiveData value

        // flip the same card again
        viewModel.flipCard(0)
        assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(-1)
        // steps never increase
        assertThat((LiveDataTestUtil.getValue(viewModel.stepsLiveData))).isEqualTo(1)
    }

    @Test
    fun flipCard_flipDifferentValueCards_stepIncreaseTwice_flipBackOneSecondLater() {
        viewModel.flipCard(0)
        assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(0)
        assertThat((LiveDataTestUtil.getValue(viewModel.stepsLiveData))).isEqualTo(1)

        // flip second card of different value
        viewModel.flipCard(2)
        assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(2)
        assertThat((LiveDataTestUtil.getValue(viewModel.stepsLiveData))).isEqualTo(2)
        viewModel.cardFlipped()

        Thread.sleep(1_000) // wait 1 second later
        assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(2)
    }

    @Test
    fun flipCard_flipSameValueCards_neverFlipBackAgain() {
        viewModel.flipCard(0)
        assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(0)
        assertThat((LiveDataTestUtil.getValue(viewModel.stepsLiveData))).isEqualTo(1)

        // flip second card of same value
        assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(1)
        assertThat((LiveDataTestUtil.getValue(viewModel.stepsLiveData))).isEqualTo(2)
        viewModel.cardFlipped()

        // after 1 second never trigger to flip back again
        Thread.sleep(1_000)
        assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(-1)
    }

    @Test
    fun flipCard_flippedAllCard_showCompletedDialog() {
        // flip all cards
        viewModel.flipCard(0)
        assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(0)
        assertThat((LiveDataTestUtil.getValue(viewModel.stepsLiveData))).isEqualTo(1)
        viewModel.flipCard(1)
        assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(1)
        assertThat((LiveDataTestUtil.getValue(viewModel.stepsLiveData))).isEqualTo(2)
        viewModel.flipCard(2)
        assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(2)
        assertThat((LiveDataTestUtil.getValue(viewModel.stepsLiveData))).isEqualTo(3)
        viewModel.flipCard(3)
        assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(3)
        assertThat((LiveDataTestUtil.getValue(viewModel.stepsLiveData))).isEqualTo(4)

        assertThat(LiveDataTestUtil.getValue(viewModel.showCompletedDialogLiveData)).isTrue()
    }

    @Test
    fun reset_differentCardsGenerated_stepResetToZero() {
        viewModel.reset()

        val newCards = LiveDataTestUtil.getValue(viewModel.cardsLiveData)
        assertThat(newCards.size).isEqualTo(cards.size)
        assertThat(newCards[0].value).isNotEqualTo(cards[0].value)
        assertThat(newCards[1].value).isNotEqualTo(cards[1].value)
        assertThat(newCards[2].value).isNotEqualTo(cards[2].value)
        assertThat(newCards[3].value).isNotEqualTo(cards[3].value)

        assertThat(LiveDataTestUtil.getValue(viewModel.stepsLiveData)).isEqualTo(0)
    }

    @Test
    fun generatedCards_alwaysDoubleOfPairValue_pairValueSetOfSameValueCards() {
        val pairSize = 3
        viewModel = CardGameViewModel(pairSize)
        viewModel.reset() // generate new set of cards

        val newCards = LiveDataTestUtil.getValue(viewModel.cardsLiveData)
        assertThat(newCards.size).isEqualTo(pairSize * 2) // double of the pair size

        val sorted = newCards.sortedBy { it.value }

        // after sorted, odd number index card value always equal to subsequent even number index card value
        for (i in sorted.indices step 2) {
            assertThat(sorted[i]).isEqualTo(sorted[i + 1])
        }
    }
}