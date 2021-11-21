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
import androidx.lifecycle.lifecycleScope
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.fadeIn
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.copyToClipboard
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleVerse
import com.duke.orca.android.kotlin.biblelockscreen.bible.share
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels.BibleVerseViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentBibleVerseBinding
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

        viewModel.get(arguments?.getInt(Key.ID) ?: 0)

        return viewBinding.root
    }

    private fun observe(binding: FragmentBibleVerseBinding) {
        viewModel.pair.observe(viewLifecycleOwner, { pair ->
            pair?.let {
                val bibleVerse = it.first
                val attributeSet = it.second

                val typeface = binding.textViewWord.typeface

                with(attributeSet) {
                    binding.textViewWord.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize)
                    binding.textViewWord.typeface = Typeface.create(typeface, if (bold) Typeface.BOLD else Typeface.NORMAL)
                    binding.textViewWord.gravity = textAlignment

                    binding.textViewBook.setTextSize(TypedValue.COMPLEX_UNIT_DIP, fontSize.minus(2))
                    binding.textViewChapter.setTextSize(
                        TypedValue.COMPLEX_UNIT_DIP,
                        fontSize.minus(2)
                    )
                    binding.textViewColon.setTextSize(
                        TypedValue.COMPLEX_UNIT_DIP,
                        fontSize.minus(2)
                    )
                    binding.textViewVerse.setTextSize(
                        TypedValue.COMPLEX_UNIT_DIP,
                        fontSize.minus(2)
                    )
                }

                FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = attributeSet.textAlignment
                    binding.linearLayout.layoutParams = this
                }

                bind(binding, bibleVerse)
            }
        })
    }

    private fun bind(binding: FragmentBibleVerseBinding, bibleVerse: BibleVerse) {
        binding.textViewWord.text = bibleVerse.word
        binding.textViewBook.text = viewModel.bibleBook.name(bibleVerse.book)
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
        bibleVerse: BibleVerse?
    ) {
        when(option) {
            options[0] -> {
                bibleVerse?.let { copyToClipboard(requireContext(), viewModel.bibleBook, it) }
                delayOnLifecycle(Duration.Delay.DISMISS) {
                    dialogFragment.dismiss()
                }
            }
            options[1] -> {
                bibleVerse?.let { share(requireContext(), viewModel.bibleBook,  it) }
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

        fun newInstance(id: Int): BibleVerseFragment {
            return BibleVerseFragment().apply {
                arguments = Bundle().apply {
                    putInt(Key.ID, id)
                }
            }
        }
    }
}