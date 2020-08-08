package com.zack.android.test.cardgame.view

import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import com.zack.android.test.cardgame.R

class CardView(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) :
    ConstraintLayout(context, attrs, defStyleAttr) {
    constructor(context: Context?, attrs: AttributeSet?) : this(context, attrs, -1)
    constructor(context: Context?) : this(context, null, -1)

    private val valueTextView: TextView
    private val frontView: CardView
    private val backView: CardView

    private val backViewOpenAnim: AnimatorSet
    private val frontViewOpenAnim: AnimatorSet
    private val backViewCloseAnim: AnimatorSet
    private val frontViewCloseAnim: AnimatorSet

    init {
        View.inflate(context, R.layout.view_card, this)
        valueTextView = findViewById(R.id.steps)
        frontView = findViewById(R.id.front_view)
        backView = findViewById(R.id.back_view)

        val scale: Float = context?.resources?.displayMetrics?.density ?: 1f
        frontView.cameraDistance = 8000 * scale
        backView.cameraDistance = 8000 * scale

        backViewOpenAnim =
            AnimatorInflater.loadAnimator(context, R.animator.open_flip_back_view) as AnimatorSet
        frontViewOpenAnim =
            AnimatorInflater.loadAnimator(context, R.animator.open_flip_front_view) as AnimatorSet
        backViewCloseAnim =
            AnimatorInflater.loadAnimator(context, R.animator.close_flip_back_view) as AnimatorSet
        frontViewCloseAnim =
            AnimatorInflater.loadAnimator(context, R.animator.close_flip_front_view) as AnimatorSet
    }

    fun setValue(value: String) {
        valueTextView.text = value
    }

    fun setInitialView(open: Boolean) {
        if (open) {
            frontView.alpha = 1f
            backView.alpha = 0f
        } else {
            frontView.alpha = 0f
            backView.alpha = 1f
        }
    }

    fun flipCard(open: Boolean) {
        if (open) {
            frontViewOpenAnim.setTarget(frontView)
            backViewOpenAnim.setTarget(backView)
            backViewOpenAnim.start()
            frontViewOpenAnim.start()
        } else {
            frontViewCloseAnim.setTarget(frontView)
            backViewCloseAnim.setTarget(backView)
            frontViewCloseAnim.start()
            backViewCloseAnim.start()
        }
    }
}