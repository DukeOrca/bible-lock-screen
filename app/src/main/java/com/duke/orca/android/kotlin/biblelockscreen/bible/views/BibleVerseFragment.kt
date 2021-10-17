package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeIn
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.copyToClipboard
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.share
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodel.BibleVerseViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentBibleVerseBinding
import com.duke.orca.android.kotlin.biblelockscreen.datastore.DataStore
import com.duke.orca.android.kotlin.biblelockscreen.datastore.PreferencesKeys
import com.like.LikeButton
import com.like.OnLikeListener
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch

@AndroidEntryPoint
class BibleVerseFragment : BaseFragment<FragmentBibleVerseBinding>(),
    OptionChoiceDialogFragment.OnOptionChoiceListener {
    private val viewModel by viewModels<BibleVerseViewModel>()
    private val options by lazy { arrayOf(getString(R.string.copy), getString(R.string.share)) }

    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBibleVerseBinding {
        return FragmentBibleVerseBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)

        observe(viewBinding)

        lifecycleScope.launch {
            viewModel.get(arguments?.getInt(Key.ID) ?: 0)?.let {
                bind(viewBinding, it)
            }
        }

        return viewBinding.root
    }

    private fun observe(binding: FragmentBibleVerseBinding) {
        viewModel.settings.observe(viewLifecycleOwner, {
            val typeface = binding.textViewWord.typeface

            val fontSize = it[PreferencesKeys.Font.fontSize] ?: DataStore.Font.DEFAULT_FONT_SIZE
            val bold = it[PreferencesKeys.Font.bold] ?: false
            val textAlignment = it[PreferencesKeys.Font.textAlignment] ?: DataStore.Font.TextAlignment.LEFT

            binding.textViewWord.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize)
            binding.textViewWord.typeface = Typeface.create(typeface, if (bold) Typeface.BOLD else Typeface.NORMAL)
            binding.textViewWord.gravity = textAlignment

            binding.textViewBook.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize.minus(2))
            binding.textViewChapter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize.minus(2))
            binding.textViewColon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize.minus(2))
            binding.textViewVerse.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize.minus(2))

            FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = textAlignment
                binding.linearLayout.layoutParams = this
            }
        })
    }

    private fun bind(binding: FragmentBibleVerseBinding, bibleVerse: BibleVerse) {
        binding.textViewWord.text = bibleVerse.word
        binding.textViewBook.text = getBook(bibleVerse.book)
        binding.textViewChapter.text = bibleVerse.chapter.toString()
        binding.textViewVerse.text = bibleVerse.verse.toString()

        binding.likeButton.isLiked = bibleVerse.favorites
        binding.likeButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton?) {
                addToFavorites(bibleVerse.id)
            }

            override fun unLiked(likeButton: LikeButton?) {
                deleteFavorites(bibleVerse.id)
            }
        })

        binding.imageViewMoreVert.setOnClickListener {
            OptionChoiceDialogFragment.newInstance(options, bibleVerse).also {
                it.show(childFragmentManager, it.tag)
            }
        }

        binding.constraintLayout.fadeIn(Duration.SHORT)
    }

    private fun addToFavorites(id: Int) {
        viewModel.updateFavorites(id, true)
    }

    private fun deleteFavorites(id: Int) {
        viewModel.updateFavorites(id, false)
    }

    override fun onOptionChoice(
        dialogFragment: DialogFragment,
        option: String,
        bibleVerse: BibleVerse?
    ) {
        when(option) {
            options[0] -> {
                bibleVerse?.let { copyToClipboard(requireContext(), it) }
                delayOnLifecycle(Duration.SHORT) {
                    dialogFragment.dismiss()
                }
            }
            options[1] -> {
                bibleVerse?.let { share(requireContext(), it) }
                delayOnLifecycle(Duration.SHORT) {
                    dialogFragment.dismiss()
                }
            }
        }
    }

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.bible.views"

        private object Key {
            const val ID = "$PACKAGE_NAME.ID"
        }

        fun newInstance(id: Int): BibleVerseFragment {
            return BibleVerseFragment().apply {
                arguments = Bundle().apply {
                    putInt(Key.ID, id)
                }
            }
        }
    }
}