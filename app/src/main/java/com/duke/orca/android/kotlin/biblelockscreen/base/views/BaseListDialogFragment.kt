package com.duke.orca.android.kotlin.biblelockscreen.base.views

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import androidx.viewbinding.ViewBinding
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.databinding.FragmentBaseListDialogBinding

abstract class BaseListDialogFragment<VB : ViewBinding, T> : BaseDialogFragment<FragmentBaseListDialogBinding>() {
    abstract fun inflateItemView(inflater: LayoutInflater, container: ViewGroup): VB
    abstract fun bind(viewBinding: VB, item: T)
    abstract val items: Array<T>

    override val setWindowAnimation: Boolean
        get() = false

    override fun inflate(inflater: LayoutInflater, container: ViewGroup?): FragmentBaseListDialogBinding {
        return FragmentBaseListDialogBinding.inflate(inflater, container, false)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)

        viewBinding.recyclerView.apply {
            adapter = Adapter(items)
            layoutManager = LinearLayoutManagerWrapper(requireContext())
        }

        return viewBinding.root
    }

    inner class Adapter(private val items: Array<T>): RecyclerView.Adapter<Adapter.ViewHolder>() {
        inner class ViewHolder(val viewBinding: VB): RecyclerView.ViewHolder(viewBinding.root)

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            return ViewHolder(inflateItemView(layoutInflater, parent))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            bind(holder.viewBinding, items[position])
        }

        override fun getItemCount(): Int = items.count()
    }
}