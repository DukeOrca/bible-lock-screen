package com.duke.orca.android.kotlin.biblelockscreen.base.views

import android.os.Bundle
import androidx.annotation.CallSuper
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.application.isNotNull
import com.duke.orca.android.kotlin.biblelockscreen.bible.views.BibleChapterPagerFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.views.BibleVerseSearchFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.views.FavoritesFragment
import com.duke.orca.android.kotlin.biblelockscreen.settings.views.FontSettingsFragment
import com.duke.orca.android.kotlin.biblelockscreen.settings.views.LockScreenSettingsFragment
import com.duke.orca.android.kotlin.biblelockscreen.settings.views.SettingsFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class FragmentContainerActivity : BaseLockScreenActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_fragment_container)

        if (savedInstanceState.isNotNull()) {
            return
        }

        intent?.getStringExtra(Extra.SIMPLE_NAME)?.let {
            if (it.isBlank()) {
                finish()
            } else {
                replaceFragment(it)
            }
        } ?: finish()
    }

    @CallSuper
    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.z_adjustment_bottom, R.anim.slide_out_right)
    }

    private fun replaceFragment(simpleName: String) {
        val fragment = when (simpleName) {
            BibleChapterPagerFragment::class.java.simpleName -> BibleChapterPagerFragment()
            BibleVerseSearchFragment::class.java.simpleName -> BibleVerseSearchFragment()
            FavoritesFragment::class.java.simpleName -> FavoritesFragment()
            FontSettingsFragment::class.java.simpleName -> FontSettingsFragment()
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

        object Extra {
            private const val OBJECT_NAME = "Extra"
            const val SIMPLE_NAME = "$PACKAGE_NAME.$OBJECT_NAME.SIMPLE_NAME"
        }
    }
}