package com.zack.android.test.cardgame.activity

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.zack.android.test.cardgame.R
import com.zack.android.test.cardgame.adapter.CardsRecyclerViewAdapter
import com.zack.android.test.cardgame.data.Card
import com.zack.android.test.cardgame.view.CardView
import com.zack.android.test.cardgame.viewmodel.CardGameViewModel
import com.zack.android.test.cardgame.viewmodel.CardGameViewModelFactory

class CardGameActivity : AppCompatActivity() {
    companion object {
        const val CARD_PAIRS_VALUE = 6
    }

    private lateinit var steps: TextView
    private lateinit var restart: TextView
    private lateinit var cardsView: RecyclerView
    private var adapter: CardsRecyclerViewAdapter? = null
    private lateinit var viewModel: CardGameViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_card_game)
        steps = findViewById(R.id.steps)
        restart = findViewById(R.id.restart)
        cardsView = findViewById(R.id.cards_view)
        val layoutManager = GridLayoutManager(this, resources.getInteger(R.integer.num_of_col))
        cardsView.layoutManager = layoutManager

        viewModel = ViewModelProvider(this, CardGameViewModelFactory(CARD_PAIRS_VALUE))
            .get(CardGameViewModel::class.java)
        setObserver()
        setListener()
    }

    private fun setListener() {
        restart.setOnClickListener {
            viewModel.reset()
        }
    }

    private fun setObserver() {
        viewModel.cardsLiveData.observe(this, Observer {
            if (!it.isNullOrEmpty()) {
                if (adapter == null) {
                    createAdapter(it)
                } else {
                    adapter!!.resetCards(it)
                }
            }
        })
        viewModel.closeAllCardsLiveData.observe(this, Observer {positions ->
            positions?.let {
                closeRevealedCards(it)
                viewModel.closedAllCards()
            }
        })
        viewModel.stepsLiveData.observe(this, Observer {
            steps.text = it.toString()
        })
        viewModel.flipCardLiveData.observe(this, Observer {
            if (it != -1) {
                flipCardAtPosition(it)
                viewModel.cardFlipped()
            }
        })
        viewModel.showCompletedDialogLiveData.observe(this, Observer {
            Toast.makeText(this, "Completed!", Toast.LENGTH_LONG).show()
        })
    }

    private fun closeRevealedCards(positions: MutableList<Int>) {
        for(pos in positions) {
            val holder = cardsView.findViewHolderForAdapterPosition(pos)
            if (holder != null) {
                (holder.itemView as CardView).flipCard(false)
            }
        }
    }

    private fun flipCardAtPosition(position: Int) {
        val card = adapter?.getItem(position)
        val holder = cardsView.findViewHolderForAdapterPosition(position)
        card?.let {
            (holder?.itemView as CardView).flipCard(it.revealed)
        }
    }

    private fun createAdapter(cards: MutableList<Card>) {
        adapter = CardsRecyclerViewAdapter(cards) {position ->
            viewModel.flipCard(position)
        }
        cardsView.adapter = adapter
    }
}