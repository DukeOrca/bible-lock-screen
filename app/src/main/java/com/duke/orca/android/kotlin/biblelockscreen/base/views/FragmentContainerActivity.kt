package com.duke.orca.android.kotlin.biblelockscreen.base.views

import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.CallSuper
import androidx.appcompat.app.AppCompatDelegate
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.EXTRA_SIMPLE_NAME
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.application.notNull
import com.duke.orca.android.kotlin.biblelockscreen.base.viewmodels.FragmentContainerViewModel
import com.duke.orca.android.kotlin.biblelockscreen.bible.views.BibleVerseSearchFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.views.ChapterPagerFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.views.FavoritesFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.views.HighlightsFragment
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.settings.views.FontSettingsFragment
import com.duke.orca.android.kotlin.biblelockscreen.settings.views.LockScreenSettingsFragment
import com.duke.orca.android.kotlin.biblelockscreen.settings.views.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentContainerActivity : BaseLockScreenActivity() {
    private val viewModel by viewModels<FragmentContainerViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        setDarkMode(savedInstanceState)

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)

        if (savedInstanceState.notNull) {
            return
        }

        viewModel.activityResult.observe(this, {
            setResult(it.resultCode, it.data)
        })

        intent?.getStringExtra(EXTRA_SIMPLE_NAME)?.let {
            if (it.isBlank()) {
                finish()
            } else {
                replaceFragment(it)
            }
        } ?: finish()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putBoolean(Key.RECREATE, true)
    }

    private fun setDarkMode(savedInstanceState: Bundle?) {
        savedInstanceState?.let {
            if (it.getBoolean(Key.RECREATE)) {
                if (DataStore.Display.isDarkMode(this)) {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
                } else {
                    AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
                }
            }
        }
    }

    @CallSuper
    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.z_adjustment_bottom, R.anim.slide_out_right)
    }

    private fun replaceFragment(simpleName: String) {
        val fragment = when (simpleName) {
            BibleVerseSearchFragment::class.java.simpleName -> BibleVerseSearchFragment()
            ChapterPagerFragment::class.java.simpleName -> ChapterPagerFragment()
            FavoritesFragment::class.java.simpleName -> FavoritesFragment()
            FontSettingsFragment::class.java.simpleName -> FontSettingsFragment()
            HighlightsFragment::class.java.simpleName -> HighlightsFragment()
            LockScreenSettingsFragment::class.java.simpleName -> LockScreenSettingsFragment()
            SettingsFragment::class.java.simpleName -> SettingsFragment()
            else -> throw IllegalArgumentException()
        }

        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container_view, fragment, fragment.tag)
            .commit()
    }

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.base.views"

        object Key {
            private const val OBJECT_NAME = "Key"
            const val RECREATE = "$PACKAGE_NAME.$OBJECT_NAME.RECREATE"
        }
    }
}