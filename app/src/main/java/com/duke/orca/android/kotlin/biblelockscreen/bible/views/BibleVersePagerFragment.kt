package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.annotation.SuppressLint
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.activity.OnBackPressedCallback
import androidx.core.view.GravityCompat.END
import androidx.drawerlayout.widget.DrawerLayout
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.transition.AutoTransition
import androidx.transition.Transition
import androidx.transition.TransitionManager
import androidx.viewpager2.widget.ViewPager2
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.admob.AdLoader
import com.duke.orca.android.kotlin.biblelockscreen.application.*
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.*
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.ONE_SECOND
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.PageMargin
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseFragment
import com.duke.orca.android.kotlin.biblelockscreen.base.views.FragmentContainerActivity
import com.duke.orca.android.kotlin.biblelockscreen.bible.adapters.BibleVersePagerAdapter
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.pagetransformer.PageTransformer
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodel.BibleVersePagerViewModel
import com.duke.orca.android.kotlin.biblelockscreen.billing.listener.RemoveAdsPurchaseStateListener
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentBibleVersePagerBinding
import com.duke.orca.android.kotlin.biblelockscreen.databinding.NativeAdBinding
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.devicecredential.DeviceCredential
import com.duke.orca.android.kotlin.biblelockscreen.devicecredential.annotation.RequireDeviceCredential
import com.duke.orca.android.kotlin.biblelockscreen.review.Review
import com.duke.orca.android.kotlin.biblelockscreen.settings.views.FontSettingsFragment
import com.duke.orca.android.kotlin.biblelockscreen.settings.views.LockScreenSettingsFragment
import com.duke.orca.android.kotlin.biblelockscreen.settings.views.SettingsFragment
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.material.navigation.NavigationView
import dagger.hilt.android.AndroidEntryPoint
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.subjects.PublishSubject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.util.concurrent.TimeUnit
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

@AndroidEntryPoint
@RequireDeviceCredential
class BibleVersePagerFragment : BaseFragment<FragmentBibleVersePagerBinding>(),
    RemoveAdsPurchaseStateListener,
    NavigationView.OnNavigationItemSelectedListener {
    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBibleVersePagerBinding {
        return FragmentBibleVersePagerBinding.inflate(inflater, container, false)
    }

    private object Unlock {
        const val endRange = 600.0F
        var outOfEndRange = false
        var x = 0.0F
        var y = 0.0F
    }

    private val viewModel by viewModels<BibleVersePagerViewModel>()
    private val bibleVersePagerAdapter by lazy { BibleVersePagerAdapter(this) }
    private val drawerLayout by lazy { viewBinding.drawerLayout }

    private var currentItem: BibleVerse? = null
    private var nativeAd: NativeAd? = null
    private var nativeAdBinding: NativeAdBinding? = null

    private val onPageChangeCallback = object : ViewPager2.OnPageChangeCallback() {
        override fun onPageSelected(position: Int) {
            super.onPageSelected(position)
            viewModel.get(position)
        }
    }

    private val onBackPressedCallback by lazy {
        object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                if (drawerLayout.isDrawerOpen(END)) {
                    drawerLayout.closeDrawer(END)
                } else {
                    if (childFragmentManager.backStackEntryCount > 0) {
                        childFragmentManager.popBackStackImmediate()
                    } else {
                        val unlockWithBackKey = DataStore.LockScreen.getUnlockWithBackKey(requireContext())

                        if (unlockWithBackKey) {
                            finish()
                        } else {
                            viewBinding.frameLayoutUnlock.animateRipple()
                        }
                    }
                }
            }
        }
    }

    private val autoTransition by lazy {
        AutoTransition().apply {
            duration = Duration.SHORT
            this.addListener(object : Transition.TransitionListener {
                override fun onTransitionStart(transition: Transition) {
                    viewBinding.imageViewMenu.disable()
                }

                override fun onTransitionEnd(transition: Transition) {
                    viewBinding.imageViewMenu.enable()
                }

                override fun onTransitionCancel(transition: Transition) {
                    viewBinding.imageViewMenu.enable()
                }

                override fun onTransitionPause(transition: Transition) {
                    viewBinding.imageViewMenu.enable()
                }

                override fun onTransitionResume(transition: Transition) {
                    viewBinding.imageViewMenu.disable()
                }
            })
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        requireActivity().onBackPressedDispatcher.addCallback(this, onBackPressedCallback)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        putPublishSubject(Key.NATIVE_AD_VIEW_VISIBILITY, PublishSubject.create())
        putPublishSubject(Key.NEW_STATE, PublishSubject.create())

        observe()
        subscribe()
        bind()

        return viewBinding.root
    }

    override fun onDestroyView() {
        DataStore.BibleVerse.putCurrentItem(requireContext(), viewBinding.viewPager2.currentItem)
        nativeAd?.destroy()
        viewBinding.viewPager2.unregisterOnPageChangeCallback(onPageChangeCallback)
        super.onDestroyView()
    }

    override fun onDetach() {
        onBackPressedCallback.remove()
        super.onDetach()
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.item_search -> addFragment(BibleVerseSearchFragment::class.java.simpleName)
            R.id.item_favorites -> addFragment(FavoritesFragment::class.java.simpleName)
            R.id.item_bible -> addFragment(BibleChapterPagerFragment::class.java.simpleName)
            R.id.item_settings -> addFragment(SettingsFragment::class.java.simpleName)
            R.id.item_lock_screen -> addFragment(LockScreenSettingsFragment::class.java.simpleName)
            R.id.item_font -> addFragment(FontSettingsFragment::class.java.simpleName)
            R.id.item_share_the_app -> shareApplication(requireContext())
            R.id.item_write_review -> Review.launchReviewFlow(requireActivity())
        }

        delayOnLifecycle(Duration.LONG) {
            if (viewBinding.drawerLayout.isDrawerOpen(Gravity.RIGHT)) {
                viewBinding.drawerLayout.closeDrawer(Gravity.RIGHT)
            }
        }

        return true
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun bind() {
        viewBinding.navigationView.setNavigationItemSelectedListener(this)

        viewBinding.drawerLayout.addDrawerListener(object : DrawerLayout.DrawerListener {
            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {
            }

            override fun onDrawerOpened(drawerView: View) {
            }

            override fun onDrawerClosed(drawerView: View) {
            }

            override fun onDrawerStateChanged(newState: Int) {
                getPublishSubject(Key.NEW_STATE).onNext(newState)
            }
        })

        viewBinding.imageViewSearch.setOnClickListener {
            addFragment(BibleVerseSearchFragment::class.java.simpleName)
        }

        viewBinding.imageViewFavorite.setOnClickListener {
            addFragment(FavoritesFragment::class.java.simpleName)
        }

        viewBinding.imageViewMenu.setOnClickListener {
            with(drawerLayout) {
                if (isDrawerVisible(END).not()) {
                    openDrawer(END)
                }
            }
        }

        viewBinding.exposedDropdownMenuBook.autoCompleteTextView.freezesText = false
        viewBinding.exposedDropdownMenuChapter.autoCompleteTextView.freezesText = false
        viewBinding.exposedDropdownMenuVerse.autoCompleteTextView.freezesText = false

        viewBinding.exposedDropdownMenuBook.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val chapter = 1
            val verse = 1

            moveTo(position.inc(), chapter, verse)
        }

        viewBinding.exposedDropdownMenuChapter.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val book = currentItem?.book ?: 1
            val verse = 1

            moveTo(book, position.inc(), verse)
        }

        viewBinding.exposedDropdownMenuVerse.autoCompleteTextView.setOnItemClickListener { _, _, position, _ ->
            val book = currentItem?.book ?: 1
            val chapter = currentItem?.chapter ?: 1

            moveTo(book, chapter, position.inc())
        }

        viewBinding.constraintLayoutExposedDropdownMenu.fadeIn(Duration.LONG)

        viewBinding.viewPager2.adapter = bibleVersePagerAdapter
        viewBinding.viewPager2.offscreenPageLimit = 2
        viewBinding.viewPager2.registerOnPageChangeCallback(onPageChangeCallback)
        viewBinding.viewPager2.setCurrentItem(DataStore.BibleVerse.getCurrentItem(requireContext()), false)

        setPageTransformer(PageMargin.medium, false)

        viewBinding.viewPager2.fadeIn(Duration.LONG) {
            setPageTransformer(PageMargin.small, true) {
                with(viewBinding.viewLeftFake) {
                    viewTreeObserver.addOnDrawListener {
                        if (abs(translationX) == 0.0F) {
                            setPageTransformer(PageMargin.small, false)
                            hide(true)
                            viewBinding.viewRightFake.hide(true)
                        } else {
                            if (currentItem?.id != 0) {
                                show()
                            }
                            
                            if (currentItem?.id != VERSE_COUNT.dec()) {
                                viewBinding.viewRightFake.show()
                            }

                            setPageTransformer(PageMargin.medium, false)
                        }
                    }
                }
            }
        }

        viewBinding.linearLayoutUnlock.setOnTouchListener { _, event ->
            when(event.action) {
                MotionEvent.ACTION_DOWN -> {
                    disableUserInput()
                    Unlock.x = event.x
                    Unlock.y = event.y
                }
                MotionEvent.ACTION_MOVE -> {
                    viewBinding.frameLayoutUnlock.showRipple()

                    val distance = sqrt((Unlock.x - event.x).pow(2) + (Unlock.y - event.y).pow(2))
                    var scale = abs(Unlock.endRange - distance * 0.4F) / Unlock.endRange

                    when {
                        scale >= 1.0F -> scale = 1.0F
                        scale < 0.8F -> scale = 0.8F
                    }

                    val alpha = (scale - 0.80F) * 5.0F

                    viewBinding.linearLayout.alpha = alpha
                    viewBinding.linearLayout.scaleX = scale
                    viewBinding.linearLayout.scaleY = scale
                    viewBinding.imageViewUnlock.alpha = alpha
                    viewBinding.imageViewUnlock.scaleX = scale
                    viewBinding.imageViewUnlock.scaleY = scale
                    viewBinding.nativeAd.root.alpha = alpha

                    translateFakeViews(alpha)

                    Unlock.outOfEndRange = distance * 1.25F > Unlock.endRange * 0.75F
                }
                MotionEvent.ACTION_UP -> {
                    if (Unlock.outOfEndRange) {
                        if (DeviceCredential.requireUnlock(requireContext()))
                            confirmDeviceCredential()
                        else {
                            requireActivity().finish()
                        }
                    } else {
                        restoreVisibility()
                    }
                }
            }

            true
        }
    }

    private fun observe() {
        viewModel.currentItem.observe(viewLifecycleOwner, { bibleVerse ->
            bibleVerse?.let {
                viewBinding.exposedDropdownMenuBook.autoCompleteTextView.setText(
                    getBook(it.book),
                    false
                )
                viewBinding.exposedDropdownMenuChapter.autoCompleteTextView.setText(
                    it.chapter.toString(),
                    false
                )
                viewBinding.exposedDropdownMenuVerse.autoCompleteTextView.setText(
                    it.verse.toString(),
                    false
                )

                viewBinding.exposedDropdownMenuBook.autoCompleteTextView.setStringArrayAdapter(
                    books, R.layout.dropdown_item
                )

                if (currentItem?.book != it.book) {
                    viewBinding.exposedDropdownMenuChapter.autoCompleteTextView.setIntegerArrayAdapter(
                        chapters[it.book.dec()], R.layout.dropdown_item
                    )
                }

                if (currentItem?.book != it.book || currentItem?.chapter != it.chapter) {
                    lifecycleScope.launch(Dispatchers.IO) {
                        val itemCount = viewModel.getVerseCount(it.book, it.chapter)

                        withContext(Dispatchers.Main) {
                            viewBinding.exposedDropdownMenuVerse.autoCompleteTextView.setIntegerArrayAdapter(
                                itemCount, R.layout.dropdown_item
                            )
                        }
                    }
                }

                currentItem = it
            }
        })

        viewModel.removeAds.observe(viewLifecycleOwner, {
            if (it) {
                onRemoved()
            } else {
                onPending()
            }
        })
    }

    private fun subscribe() {
        compositeDisposable.add(Observable.combineLatest(
            getPublishSubject(Key.NATIVE_AD_VIEW_VISIBILITY).distinctUntilChanged(),
            getPublishSubject(Key.NEW_STATE).filter {
                if (it is Int) {
                    viewModel.newState = it
                }

                it == DrawerLayout.STATE_IDLE
            }, { visibility, _ ->
                visibility
            }
        ).subscribe {
            if (it == View.VISIBLE) {
                showNativeAdView()
            } else {
                hideNativeAdView()
            }
        })

        viewModel.newState?.let {
            getPublishSubject(Key.NEW_STATE).onNext(it)
        } ?: getPublishSubject(Key.NEW_STATE).onNext(DrawerLayout.STATE_IDLE)
    }

    private fun addFragment(simpleName: String) {
        Intent(requireContext(), FragmentContainerActivity::class.java).also {
            it.putExtra(FragmentContainerActivity.Companion.Extra.SIMPLE_NAME, simpleName)
            startActivity(it)
            overridePendingTransition(R.anim.slide_in_right, R.anim.hold)
        }
    }

    private fun moveTo(book: Int, chapter: Int, verse: Int) {
        lifecycleScope.launch(Dispatchers.IO) {
            val item = viewModel.get(book, chapter, verse).id

            withContext(Dispatchers.Main) {
                viewBinding.viewPager2.setCurrentItem(item, false)
            }
        }
    }

    private fun confirmDeviceCredential() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            DeviceCredential.confirmDeviceCredential(requireActivity(), object : KeyguardManager.KeyguardDismissCallback() {
                override fun onDismissCancelled() {
                    super.onDismissCancelled()
                    restoreVisibility()
                }

                override fun onDismissError() {
                    super.onDismissError()
                    restoreVisibility()
                }

                override fun onDismissSucceeded() {
                    super.onDismissSucceeded()
                    requireActivity().finish()
                }
            })
        } else {
            DeviceCredential.confirmDeviceCredential(
                this,
                getActivityResultLauncher(Key.DEVICE_CREDENTIAL)
            )

            restoreVisibility()
        }
    }

    private fun setPageTransformer(
        pageMargin: Float,
        scheduleAnimation: Boolean,
        onPageAnimationEnd: (() -> Unit)? = null
    ) {
        viewBinding.viewPager2.setPageTransformer(PageTransformer(
            pageMargin,
            scheduleAnimation
        ).apply {
            onPageAnimationEnd?.let {
                setPageAnimatorListener(object : PageTransformer.PageAnimatorListener {
                    override fun onPageAnimationEnd() {
                        it.invoke()
                    }
                })
            }
        })
    }

    private fun translateFakeViews(alpha: Float) {
        viewBinding.viewLeftFake.alpha = alpha
        viewBinding.viewRightFake.alpha = alpha

        viewBinding.viewLeftFake.translationX = ((1.0F - alpha) * -8.0F).toPx
        viewBinding.viewRightFake.translationX = ((1.0F - alpha) * 8.0F).toPx
    }

    private fun restoreVisibility() {
        viewBinding.frameLayoutUnlock.hideRipple()
        viewBinding.imageViewUnlock.scale(1.0F, duration = Duration.MEDIUM)
        viewBinding.linearLayout.scale(1.0F, duration =  Duration.MEDIUM)
        viewBinding.viewLeftFake.translateX(0.0F, duration = Duration.MEDIUM)
        viewBinding.viewRightFake.translateX(0.0F, duration = Duration.MEDIUM) {
            enableUserInput()
        }

        nativeAd?.let {
            viewBinding.nativeAd.root.fade(1.0F, duration = Duration.MEDIUM)
        }
    }

    private fun disableUserInput() {
        viewBinding.viewPager2.isUserInputEnabled = false
    }

    private fun enableUserInput() {
        viewBinding.viewPager2.isUserInputEnabled = true
    }

    private fun populateNativeAdView() {
        AdLoader.loadNativeAd(requireContext()) {
            try {
                with(NativeAdBinding.bind(viewBinding.nativeAd.root)) {
                    nativeAd = it
                    nativeAdBinding = NativeAdBinding.bind(viewBinding.nativeAd.root)
                    AdLoader.populateNativeAdView(this, it)
                }

                getPublishSubject(Key.NATIVE_AD_VIEW_VISIBILITY).onNext(View.VISIBLE)
            } catch (e: NullPointerException) {
                Timber.e(e)
            }
        }
    }

    private fun showNativeAdView() {
        try {
            nativeAdBinding?.let {
                TransitionManager.beginDelayedTransition(viewBinding.constraintLayoutRoot, autoTransition)
                it.root.show()
            }
        } catch (e: NullPointerException) {
            Timber.e(e)
            viewBinding.imageViewMenu.enable()
        }
    }

    private fun hideNativeAdView() {
        try {
            nativeAdBinding?.let {
                TransitionManager.beginDelayedTransition(viewBinding.constraintLayoutRoot, autoTransition)
                it.root.hide()
            }
        } catch (e: NullPointerException) {
            Timber.e(e)
            viewBinding.imageViewMenu.enable()
        }
    }

    override fun onPending() {
        viewBinding.imageViewMenu.disable()

        val begin = DataStore.getBegin(requireContext())
        val days = TimeUnit.MILLISECONDS.toDays(System.currentTimeMillis() - begin)

        if (days > -7) { // todo test.
            populateNativeAdView()
        }
    }

    override fun onRemoved() {
        try {
            viewBinding.imageViewMenu.disable()

            nativeAd?.destroy()
            getPublishSubject(Key.NATIVE_AD_VIEW_VISIBILITY).onNext(View.GONE)
        } catch (e: NullPointerException) {
            Timber.e(e)
        }
    }

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.bible.views"

        private object Key {
            const val DEVICE_CREDENTIAL = "$PACKAGE_NAME.DEVICE_CREDENTIAL"
            const val NATIVE_AD_VIEW_VISIBILITY = "$PACKAGE_NAME.NATIVE_AD_VIEW_VISIBILITY"
            const val NEW_STATE = "$PACKAGE_NAME.NEW_STATE"
        }
    }
}