package com.zack.android.test.cardgame.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.google.common.truth.Truth.assertThat
import com.zack.android.test.cardgame.LiveDataTestUtil
import com.zack.android.test.cardgame.data.Card
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class CardGameViewModelTest {
    @get:Rule
    var instantExecutorRule = InstantTaskExecutorRule()

    private val testDispatcher = TestCoroutineDispatcher()

    private lateinit var viewModel: CardGameViewModel
    private val cards = listOf(
        Card(-1),
        Card(-1),
        Card(-2),
        Card(-2)
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        viewModel = CardGameViewModel(2)
        viewModel.setSampleCards(cards)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
        testDispatcher.cleanupTestCoroutines()
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
    fun flipCard_flipDifferentValueCards_stepIncreaseTwice_flipBackOneSecondLater() =
        testDispatcher.runBlockingTest {
            viewModel.flipCard(0)
            assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(0)
            assertThat((LiveDataTestUtil.getValue(viewModel.stepsLiveData))).isEqualTo(1)

            // flip second card of different value
            viewModel.flipCard(2)
            assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(2)
            assertThat((LiveDataTestUtil.getValue(viewModel.stepsLiveData))).isEqualTo(2)
            viewModel.cardFlipped()

            // flip card is triggered after 1 second delay
            advanceTimeBy(1_000)
            assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(2)
        }

    @Test
    fun flipCard_flipSameValueCards_neverFlipBackAgain() = testDispatcher.runBlockingTest {
        viewModel.flipCard(0)
        assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(0)
        assertThat((LiveDataTestUtil.getValue(viewModel.stepsLiveData))).isEqualTo(1)

        // flip second card of same value
        viewModel.flipCard(1)
        assertThat(LiveDataTestUtil.getValue(viewModel.flipCardLiveData)).isEqualTo(1)
        assertThat((LiveDataTestUtil.getValue(viewModel.stepsLiveData))).isEqualTo(2)
        viewModel.cardFlipped()

        // after 1 second never trigger to flip back again
        advanceTimeBy(1_000)
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
        viewModel.dialogShown()
        assertThat(LiveDataTestUtil.getValue(viewModel.showCompletedDialogLiveData)).isFalse()
    }

    @Test
    fun reset_closeRevealedCards() = testDispatcher.runBlockingTest{
        viewModel.flipCard(0)
        viewModel.flipCard(1)

        viewModel.reset()
        val positions = LiveDataTestUtil.getValue(viewModel.closeAllCardsLiveData)
        assertThat(positions).isNotNull()
        positions?.let {
            assertThat(it[0]).isEqualTo(0)
            assertThat(it[1]).isEqualTo(1)
        }

        viewModel.closedAllCards()
        assertThat(LiveDataTestUtil.getValue(viewModel.closeAllCardsLiveData)).isNull()
    }

    @Test
    fun reset_differentCardsGenerated_stepResetToZero() = testDispatcher.runBlockingTest {
        viewModel.reset()

        assertThat(LiveDataTestUtil.getValue(viewModel.stepsLiveData)).isEqualTo(0)

        advanceTimeBy(600)
        val newCards = LiveDataTestUtil.getValue(viewModel.cardsLiveData)
        assertThat(newCards.size).isEqualTo(cards.size)
        assertThat(newCards[0].value).isNotEqualTo(cards[0].value)
        assertThat(newCards[1].value).isNotEqualTo(cards[1].value)
        assertThat(newCards[2].value).isNotEqualTo(cards[2].value)
        assertThat(newCards[3].value).isNotEqualTo(cards[3].value)
    }

    @Test
    fun generatedCards_alwaysDoubleOfPairValue_pairValueSetOfSameValueCards() = testDispatcher.runBlockingTest{
        val pairSize = 3
        viewModel = CardGameViewModel(pairSize)
        viewModel.reset() // generate new set of cards

        advanceTimeBy(600)
        val newCards = LiveDataTestUtil.getValue(viewModel.cardsLiveData)
        assertThat(newCards.size).isEqualTo(pairSize * 2) // double of the pair size

        val sorted = newCards.sortedBy { it.value }

        // after sorted, odd number index card value always equal to subsequent even number index card value
        for (i in sorted.indices step 2) {
            assertThat(sorted[i]).isEqualTo(sorted[i + 1])
        }
    }
}