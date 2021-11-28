package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.graphics.Typeface
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.isVisible
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeIn
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.copyToClipboard
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.Verse
import com.duke.orca.android.kotlin.biblelockscreen.bible.share
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels.BibleVerseViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentBibleVerseBinding
import com.like.LikeButton
import com.like.OnLikeListener
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class VerseFragment : BaseFragment<FragmentBibleVerseBinding>(),
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

        viewModel.get(arguments?.getInt(Key.ID) ?: 0)

        return viewBinding.root
    }

    private fun observe(binding: FragmentBibleVerseBinding) {
        viewModel.pair.observe(viewLifecycleOwner, { pair ->
            pair?.let {
                val verse = it.first
                val font = it.second

                val typeface = binding.textViewWord.typeface

                with(font) {
                    binding.textViewWord.setTextSize(TypedValue.COMPLEX_UNIT_DIP, size)
                    binding.textViewWord.typeface = Typeface.create(typeface, if (bold) Typeface.BOLD else Typeface.NORMAL)
                    binding.textViewWord.gravity = textAlignment


                    with(size.minus(
                        when {
                            size > 16.0f -> 4
                            size > 24.0f -> 8
                            else -> 2
                        }
                    )) {
                        binding.textViewBook.setTextSize(TypedValue.COMPLEX_UNIT_DIP, this)
                        binding.textViewChapter.setTextSize(TypedValue.COMPLEX_UNIT_DIP, this)
                        binding.textViewColon.setTextSize(TypedValue.COMPLEX_UNIT_DIP, this)
                        binding.textViewVerse.setTextSize(TypedValue.COMPLEX_UNIT_DIP, this)
                    }
                }

                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = font.textAlignment
                    binding.linearLayout.layoutParams = this
                }

                bind(binding, verse)
            }
        })
    }

    private fun bind(binding: FragmentBibleVerseBinding, verse: Verse) {
        binding.textViewWord.text = verse.word
        binding.textViewBook.text = viewModel.bibleBook.name(verse.book)
        binding.textViewChapter.text = verse.chapter.toString()
        binding.textViewVerse.text = verse.verse.toString()

        binding.likeButton.isLiked = verse.favorite
        binding.likeButton.setOnLikeListener(object : OnLikeListener {
            override fun liked(likeButton: LikeButton?) {
                addToFavorites(verse.id)
            }

            override fun unLiked(likeButton: LikeButton?) {
                deleteFavorites(verse.id)
            }
        })

        binding.imageViewMoreVert.setOnClickListener {
            OptionChoiceDialogFragment.newInstance(options, verse).also {
                it.show(childFragmentManager, it.tag)
            }
        }

        if (binding.nestedScrollView.isVisible.not()) {
            binding.nestedScrollView.fadeIn(Duration.FADE_IN)
        }
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
        verse: Verse?
    ) {
        when(option) {
            options[0] -> {
                verse?.let { copyToClipboard(requireContext(), viewModel.bibleBook, it) }
                delayOnLifecycle(Duration.Delay.DISMISS) {
                    dialogFragment.dismiss()
                }
            }
            options[1] -> {
                verse?.let { share(requireContext(), viewModel.bibleBook,  it) }
                delayOnLifecycle(Duration.Delay.DISMISS) {
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

        fun newInstance(id: Int): VerseFragment {
            return VerseFragment().apply {
                arguments = Bundle().apply {
                    putInt(Key.ID, id)
                }
            }
        }
    }
}