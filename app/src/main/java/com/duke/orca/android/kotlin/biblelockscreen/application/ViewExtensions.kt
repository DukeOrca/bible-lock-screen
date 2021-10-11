package com.duke.orca.android.kotlin.biblelockscreen.application

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.graphics.PorterDuff
import android.graphics.drawable.RippleDrawable
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.view.View
import android.view.View.MeasureSpec
import android.view.ViewPropertyAnimator
import android.view.animation.Animation
import android.view.animation.DecelerateInterpolator
import android.view.animation.Transformation
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat

fun FrameLayout.animateRipple() {
    if (foreground is RippleDrawable) {
        val handler = Handler(Looper.getMainLooper())
        val rippleDrawable = foreground

        rippleDrawable.state = intArrayOf(
            android.R.attr.state_pressed,
            android.R.attr.state_enabled
        )
        handler.postDelayed({ rippleDrawable.state = intArrayOf() }, 200)
    }
}

fun FrameLayout.hideRipple() {
    if (foreground is RippleDrawable) {
        foreground.state = intArrayOf()
    }
}

fun FrameLayout.showRipple() {
    if (foreground is RippleDrawable) {
        val rippleDrawable = foreground

        rippleDrawable.state = intArrayOf(
            android.R.attr.state_pressed,
            android.R.attr.state_enabled
        )
    }
}

fun View.collapse(duration: Long) {
    val measuredHeight: Int = this.measuredHeight

    visibility = View.VISIBLE

    val animation = object : Animation() {
        override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
            if (interpolatedTime == 1F) {
                layoutParams.height = 0
                visibility = View.GONE
            } else {
                layoutParams.height = if ((measuredHeight - (measuredHeight * interpolatedTime).toInt()) > 0)
                    measuredHeight - (measuredHeight * interpolatedTime).toInt()
                else
                    0
                requestLayout()
            }
        }

        override fun willChangeBounds(): Boolean = true
    }

    animation.duration = duration
    startAnimation(animation)
}

fun View.expand(duration: Long, onAnimationEnd: (() -> Unit)? = null) {
    val parent = this.parent
    val widthMeasureSpec = if (parent is View)
        MeasureSpec.makeMeasureSpec(parent.width, MeasureSpec.EXACTLY)
    else
        return

    val heightMeasureSpec = MeasureSpec.makeMeasureSpec(1, MeasureSpec.UNSPECIFIED)

    measure(widthMeasureSpec, heightMeasureSpec)

    val from = this.height
    val to = this.measuredHeight

    layoutParams.height = from

    show()

    val valueAnimator = ValueAnimator.ofInt(from, to)

    valueAnimator.addUpdateListener {
        layoutParams.height = it.animatedValue as Int
        alpha = it.animatedValue as Int / to.toFloat()
        requestLayout()
    }

    valueAnimator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {}

        override fun onAnimationEnd(animation: Animator?) {
            onAnimationEnd?.invoke()
        }

        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationRepeat(animation: Animator?) {}
    })

    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.duration = duration
    valueAnimator.start()
}

fun View.fadeIn(
    duration: Number,
    alphaFrom: Float = 0F,
    onAnimationEnd: (() -> Unit)? = null
) {
    this.apply {
        alpha = alphaFrom
        visibility = View.VISIBLE

        animate()
            .alpha(1F)
            .setDuration(duration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    onAnimationEnd?.invoke()
                    super.onAnimationEnd(animation)
                }
            })
    }
}

fun View.fadeOut(duration: Number, invisible: Boolean = false, onAnimationEnd: (() -> Unit)? = null) {
    this.apply {
        alpha = 1F

        animate()
            .alpha(0F)
            .setDuration(duration.toLong())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    this@fadeOut.visibility = if (invisible)
                        View.INVISIBLE
                    else
                        View.GONE

                    onAnimationEnd?.invoke()
                    super.onAnimationEnd(animation)
                }
            })
    }
}

fun View.ripple(show: Boolean) {
    if (show) {
        if (background is RippleDrawable) {
            val rippleDrawable = background

            rippleDrawable.state = intArrayOf(
                android.R.attr.state_pressed,
                android.R.attr.state_enabled
            )
        }
    } else {
        if (background is RippleDrawable) {
            background.state = intArrayOf()
        }
    }
}

fun View.rotate(
    degrees: Float, duration: Long,
    animationListenerAdapter: AnimatorListenerAdapter? = null
) {
    this.animate().rotation(degrees)
        .setDuration(duration)
        .setListener(animationListenerAdapter)
        .start()
}

fun View.scale(
    scale: Float,
    duration: Number = 150,
    alpha: Float = 1F,
    onAnimationEnd: (() -> Unit)? = null
): ViewPropertyAnimator {
    show()

    val viewPropertyAnimator = this.animate()
        .scaleX(scale)
        .scaleY(scale)
        .alpha(alpha)
        .setDuration(duration.toLong())
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                onAnimationEnd?.invoke()
                super.onAnimationEnd(animation)
            }
        })

    viewPropertyAnimator.start()

    return viewPropertyAnimator
}

fun View.hide(invisible: Boolean = false) {
    visibility = if (invisible)
        View.INVISIBLE
    else
        View.GONE
}

fun View.show() {
    visibility = View.VISIBLE
}

fun View.expand(duration: Long, to: Int, onAnimationEnd: (() -> Unit)? = null) {
    val from = this.height

    show()

    val valueAnimator = ValueAnimator.ofInt(from, to)

    valueAnimator.addUpdateListener {
        layoutParams.height = it.animatedValue as Int
        alpha = it.animatedValue as Int / to.toFloat()
        requestLayout()
    }

    valueAnimator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {}

        override fun onAnimationEnd(animation: Animator?) {
            onAnimationEnd?.invoke()
        }

        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationRepeat(animation: Animator?) {}
    })

    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.duration = duration
    valueAnimator.start()
}

fun View.collapse(duration: Long, to: Int, hide: Boolean = true, onAnimationEnd: (() -> Unit)? = null) {
    val from = height
    val valueAnimator = ValueAnimator.ofInt(from, to)

    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.addUpdateListener { animation ->
        layoutParams.height = animation.animatedValue as Int
        alpha = animation.animatedValue as Int / from.toFloat()
        requestLayout()
    }

    valueAnimator.addListener(object : Animator.AnimatorListener {
        override fun onAnimationStart(animation: Animator?) {}

        override fun onAnimationEnd(animation: Animator?) {
            if (hide)
                hide()

            onAnimationEnd?.invoke()
        }

        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationRepeat(animation: Animator?) {}
    })

    valueAnimator.interpolator = DecelerateInterpolator()
    valueAnimator.duration = duration
    valueAnimator.start()
}

fun View.measuredHeight(view: View): Int {
    val widthMeasureSpec = MeasureSpec.makeMeasureSpec(view.width, MeasureSpec.EXACTLY)
    val heightMeasureSpec = MeasureSpec.makeMeasureSpec(1, MeasureSpec.UNSPECIFIED)

    measure(widthMeasureSpec, heightMeasureSpec)

    return measuredHeight
}

fun View.setBackgroundTint(@ColorRes id: Int) {
    backgroundTintList = ContextCompat.getColorStateList(this.context, id)
}

fun ImageView.setTint(@ColorRes id: Int) {
    setColorFilter(ContextCompat.getColor(context, id), PorterDuff.Mode.SRC_IN)
}

fun CheckBox.setButtonTint(@ColorRes id: Int) {
    buttonTintList = ContextCompat.getColorStateList(this.context, id)
}

fun TextView.setTextWithSearchWord(
    text: String,
    searchWord: String,
    @ColorInt color: Int?
) {
    if (color == null) {
        this.text = text
        return
    }

    if (text.isBlank()) {
        this.text = text
        return
    }

    val hexColor = color.toHexColor()
    val htmlText = text.replaceFirst(
        searchWord,
        "<font color='$hexColor'>$searchWord</font>"
    )
    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
        this.text = Html.fromHtml(htmlText, Html.FROM_HTML_MODE_LEGACY)
    } else {
        @Suppress("DEPRECATION")
        this.text = Html.fromHtml(htmlText)
    }
}

fun AutoCompleteTextView.setIntegerArrayAdapter(itemCount: Int, @LayoutRes layoutRes: Int) {
    val intRange = 1..itemCount

    setAdapter(
        ArrayAdapter(
            context,
            layoutRes,
            intRange.map { it.toString() }
        )
    )
}