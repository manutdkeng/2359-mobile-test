package com.zack.android.test.cardgame.activity

import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import com.zack.android.test.cardgame.R
import com.zack.android.test.cardgame.view.CardView

class TestAnimationActivity : AppCompatActivity() {
    private lateinit var cardView: CardView
    private lateinit var button: Button
    private var revealed = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_animation)

        cardView = findViewById(R.id.test_card)
        cardView.setValue("100")
        button = findViewById(R.id.button)

        button.setOnClickListener {
            revealed = !revealed
            button.text = if (revealed) {
                getString(R.string.close)
            } else {
                getString(R.string.open)
            }
            cardView.flipCard(revealed)
        }
    }
}