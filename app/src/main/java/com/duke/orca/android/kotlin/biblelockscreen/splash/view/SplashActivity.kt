package com.duke.orca.android.kotlin.biblelockscreen.splash.view

import android.annotation.SuppressLint
import android.app.ActivityOptions
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeIn
import com.duke.orca.android.kotlin.biblelockscreen.main.view.MainActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@SuppressLint("CustomSplashScreen")
class SplashActivity: AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val customAnimation = ActivityOptions.makeCustomAnimation(
            this,
            android.R.anim.fade_in,
            android.R.anim.fade_out
        )

        lifecycleScope.launch(Dispatchers.Main) {
            delay(Duration.Delay.SHORT)
            startActivity(customAnimation.toBundle())
        }
    }

    private fun startActivity(options: Bundle) {
        findViewById<ImageView>(R.id.image_view)?.let {
            it.fadeIn(Duration.FADE_IN) {
                startActivity(Intent(this, MainActivity::class.java), options)
                finish()
            }
        } ?: let {
            startActivity(Intent(this, MainActivity::class.java), options)
            finish()
        }
    }
}