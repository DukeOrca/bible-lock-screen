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
import android.view.animation.*
import android.widget.*
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.annotation.LayoutRes
import androidx.core.content.ContextCompat

fun View.enable() {
    isEnabled = true
}

fun View.disable() {
    isEnabled = false
}

fun FrameLayout.animateRipple(delayMillis: Long) {
    if (foreground is RippleDrawable) {
        val handler = Handler(Looper.getMainLooper())
        val rippleDrawable = foreground

        rippleDrawable.state = intArrayOf(
            android.R.attr.state_pressed,
            android.R.attr.state_enabled
        )

        handler.postDelayed({ rippleDrawable.state = intArrayOf() }, delayMillis)
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
    duration: Long,
    alphaFrom: Float = 0.0F,
    onAnimationEnd: (() -> Unit)? = null
) {
    this.apply {
        alpha = alphaFrom
        visibility = View.VISIBLE

        animate()
            .alpha(1.0F)
            .setDuration(duration)
            .setInterpolator(DecelerateInterpolator())
            .setListener(object : AnimatorListenerAdapter() {
                override fun onAnimationEnd(animation: Animator?) {
                    onAnimationEnd?.invoke()
                }
            })
    }
}

fun View.fadeOut(duration: Long, invisible: Boolean = false, onAnimationEnd: (() -> Unit)? = null) {
    this.apply {
        alpha = 1.0F

        animate()
            .alpha(0.0F)
            .setDuration(duration)
            .setInterpolator(AccelerateInterpolator())
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
    this.animate()
        .rotation(degrees)
        .setDuration(duration)
        .setListener(animationListenerAdapter)
        .start()
}

fun View.rotate(fromDegrees: Float, toDegrees: Float, duration: Long) {
    val rotateAnimation = RotateAnimation(
        fromDegrees,
        toDegrees,
        Animation.RELATIVE_TO_SELF,
        0.5F,
        Animation.RELATIVE_TO_SELF,
        0.5F
    )

    rotateAnimation.duration = duration
    rotateAnimation.fillAfter = true

    startAnimation(rotateAnimation)
}

fun View.fade(
    alpha: Float,
    duration: Long = 200L,
    onAnimationEnd: (() -> Unit)? = null
): ViewPropertyAnimator {
    show()

    val viewPropertyAnimator = this.animate()
        .alpha(alpha)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                onAnimationEnd?.invoke()
                super.onAnimationEnd(animation)
            }
        })

    viewPropertyAnimator.start()

    return viewPropertyAnimator
}

fun View.scale(
    scale: Float,
    alpha: Float = 1.0F,
    duration: Long = 200L,
    onAnimationEnd: (() -> Unit)? = null
): ViewPropertyAnimator {
    show()

    val viewPropertyAnimator = this.animate()
        .scaleX(scale)
        .scaleY(scale)
        .alpha(alpha)
        .setDuration(duration)
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
    val htmlText = text.replace(
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

fun View.translateX(
    value: Float,
    alpha: Float = 1.0F,
    duration: Long = 150L,
    onAnimationEnd: (() -> Unit)? = null
) {
    animate()
        .translationX(value)
        .alpha(alpha)
        .setDuration(duration)
        .setListener(object : AnimatorListenerAdapter() {
            override fun onAnimationEnd(animation: Animator?) {
                onAnimationEnd?.invoke()
                super.onAnimationEnd(animation)
            }
        })
        .start()
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

fun AutoCompleteTextView.setStringArrayAdapter(stringArray: Array<String>, @LayoutRes layoutRes: Int) {
    setAdapter(
        ArrayAdapter(
            context,
            layoutRes,
            stringArray
        )
    )
}