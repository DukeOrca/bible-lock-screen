package com.duke.orca.android.kotlin.biblelockscreen.bible.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Application
import com.duke.orca.android.kotlin.biblelockscreen.application.hide
import com.duke.orca.android.kotlin.biblelockscreen.application.show
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.base.views.BaseDialogFragment
import com.duke.orca.android.kotlin.biblelockscreen.bible.model.BibleBook
import com.duke.orca.android.kotlin.biblelockscreen.bible.viewmodels.BookSelectionDialogViewModel
import com.duke.orca.android.kotlin.biblelockscreen.databinding.BookSelectionItemBinding
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentBookSelectionDialogBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BookSelectionDialogFragment : BaseDialogFragment<FragmentBookSelectionDialogBinding>() {
    override val setWindowAnimation: Boolean
        get() = false

    private val viewModel by viewModels<BookSelectionDialogViewModel>()

    override fun inflate(
        inflater: LayoutInflater,
        container: ViewGroup?
    ): FragmentBookSelectionDialogBinding {
        return FragmentBookSelectionDialogBinding.inflate(inflater, container, false)
    }

    interface LifecycleCallback {
        fun onDialogFragmentViewCreated()
        fun onDialogFragmentViewDestroyed()
    }

    interface OnBookSelectedListener {
        fun onBookSelected(dialogFragment: BookSelectionDialogFragment, item: AdapterItem.Book)
    }

    private val list: MutableList<AdapterItem> by lazy {
        val names = viewModel.book.names

        names.mapIndexed { index, text ->
            AdapterItem.Book(text, index)
        }.toMutableList()
    }

    private val indexOfGenesis = 0
    private val indexOfMatthew = 40

    private val oldTestament by lazy { getString(R.string.old_testament) }
    private val newTestament by lazy { getString(R.string.new_testament) }

    private val bookChoiceAdapter by lazy {
        BookSelectionAdapter()
    }

    private var lifecycleCallback: LifecycleCallback? = null
    private var onBookSelectedListener: OnBookSelectedListener? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        with(parentFragment) {
            if (this is LifecycleCallback) {
                lifecycleCallback = this
            }

            if (this is OnBookSelectedListener) {
                onBookSelectedListener = this
            }
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        super.onCreateView(inflater, container, savedInstanceState)
        bind()

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        lifecycleCallback?.onDialogFragmentViewCreated()
    }

    override fun onDestroyView() {
        lifecycleCallback?.onDialogFragmentViewDestroyed()
        super.onDestroyView()
    }

    private fun bind() {
        list.add(indexOfGenesis, AdapterItem.Testament(oldTestament))
        list.add(indexOfMatthew, AdapterItem.Testament(newTestament))

        bookChoiceAdapter.submitList(list)

        viewBinding.recyclerView.apply {
            adapter = bookChoiceAdapter
            layoutManager = LinearLayoutManagerWrapper(context)
            setHasFixedSize(true)
        }

        arguments?.getInt(Key.POSITION)?.let {
            var position = it

            if (it > indexOfMatthew) {
                ++position
            }

            viewBinding.recyclerView.scrollToPosition(position)
        }
    }

    inner class BookSelectionAdapter : ListAdapter<AdapterItem, BookSelectionAdapter.ViewHolder>(DiffCallback()) {
        private val colorSecondary by lazy { requireContext().getColor(R.color.secondary) }
        private val colorText by lazy { requireContext().getColor(R.color.text) }
        private val selectedItem = viewModel.book.name(arguments?.getInt(Key.POSITION) ?: 0)

        inner class ViewHolder(private val viewBinding: BookSelectionItemBinding): RecyclerView.ViewHolder(viewBinding.root) {
            fun bind(item: AdapterItem) {
                if (item is AdapterItem.Book) {
                    viewBinding.textViewTestament.hide()
                    viewBinding.textViewBook.show()
                    viewBinding.textViewBook.text = item.text

                    if (selectedItem == item.text) {
                        viewBinding.textViewBook.setTextColor(colorSecondary)
                    } else {
                        viewBinding.textViewBook.setTextColor(colorText)
                    }

                    viewBinding.root.setOnClickListener {
                        onBookSelectedListener?.onBookSelected(this@BookSelectionDialogFragment, item)
                    }
                } else {
                    viewBinding.textViewBook.hide()
                    viewBinding.textViewTestament.show()
                    viewBinding.textViewTestament.text = item.text

                    viewBinding.root.setOnClickListener(null)
                }
            }
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(BookSelectionItemBinding.inflate(layoutInflater, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(getItem(position))
        }

        override fun getItemCount(): Int = list.count()
    }

    sealed class AdapterItem {
        abstract val text: String

        data class Book(
            override val text: String,
            val index: Int
        ) : AdapterItem()

        data class Testament(
            override val text: String
        ) : AdapterItem()
    }

    class DiffCallback: DiffUtil.ItemCallback<AdapterItem>() {
        override fun areItemsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
            return oldItem.text == newItem.text
        }

        override fun areContentsTheSame(oldItem: AdapterItem, newItem: AdapterItem): Boolean {
            return oldItem == newItem
        }
    }

    companion object {
        private const val PACKAGE_NAME = "${Application.PACKAGE_NAME}.bible.views"

        private object Key {
            const val POSITION = "$PACKAGE_NAME.POSITION"
        }

        fun newInstance(position: Int): BookSelectionDialogFragment {
            return BookSelectionDialogFragment().apply {
                arguments = Bundle().apply {
                    putInt(Key.POSITION, position)
                }
            }
        }
    }
}