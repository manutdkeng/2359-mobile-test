package com.zack.android.test.cardgame.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.zack.android.test.cardgame.data.Card
import com.zack.android.test.cardgame.view.CardView

class CardsRecyclerViewAdapter(
    cards: MutableList<Card>,
    private val itemClicked: ((position: Int) -> Any)? = null
) : RecyclerView.Adapter<CardsRecyclerViewAdapter.CardViewHolder>() {
    private val cards = mutableListOf<Card>()

    init {
        this.cards.addAll(cards)
    }

    class CardViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun resetCards(newCards: List<Card>) {
        cards.clear()
        cards.addAll(newCards)
        notifyDataSetChanged()
    }

    fun getItem(position: Int): Card {
        return cards[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CardViewHolder {
        val cardView = CardView(parent.context)
        return CardViewHolder(cardView)
    }

    override fun getItemCount(): Int {
        return cards.size
    }

    override fun onBindViewHolder(holder: CardViewHolder, position: Int) {
        val card = cards[position]
        val cardView = holder.itemView as CardView
        cardView.setValue(card.value.toString())
        cardView.setInitialView(card.revealed)
        cardView.setOnClickListener {
            itemClicked?.invoke(position)
        }
    }

}