package com.duke.orca.android.kotlin.biblelockscreen.widget

import android.content.Context
import android.content.res.TypedArray
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.PopupWindow
import androidx.recyclerview.widget.RecyclerView
import com.duke.orca.android.kotlin.biblelockscreen.R
import com.duke.orca.android.kotlin.biblelockscreen.application.constants.Duration
import com.duke.orca.android.kotlin.biblelockscreen.application.rotate
import com.duke.orca.android.kotlin.biblelockscreen.base.LinearLayoutManagerWrapper
import com.duke.orca.android.kotlin.biblelockscreen.databinding.DropdownItemBinding
import com.duke.orca.android.kotlin.biblelockscreen.databinding.DropdownMenuBinding
import com.duke.orca.android.kotlin.biblelockscreen.databinding.PopupWindowBinding
import kotlinx.coroutines.*
import timber.log.Timber

class DropdownMenu : FrameLayout {
    constructor(context: Context) : super(context) {
        bind()
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        bind()
        getAttrs(attrs)
    }

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(
        context,
        attrs,
        defStyleAttr
    ) {
        bind()
        getAttrs(attrs, defStyleAttr)
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, item: String)
    }

    private val job = Job()
    private val coroutineScope = CoroutineScope(Dispatchers.Main + job)

    override fun onDetachedFromWindow() {
        job.cancel()
        super.onDetachedFromWindow()
    }

    fun setOnItemClickListener(onItemClick: (position: Int, item: String) -> Unit) {
        onItemClickListener = object : OnItemClickListener {
            override fun onItemClick(position: Int, item: String) {
                coroutineScope.launch {
                    onItemClick(position, item)

                    coroutineScope.launch {
                        delay(Duration.Delay.DISMISS)
                        popupWindow.dismiss()
                        setText(item)
                    }
                }
            }
        }

        onItemClickListener?.let {
            arrayAdapter?.setOnItemClickListener(it)
        }
    }

    private val layoutInflater by lazy { LayoutInflater.from(context) }
    private val viewBinding by lazy { DropdownMenuBinding.inflate(layoutInflater, this, false) }

    private val popupWindow by lazy {
        PopupWindow().apply {
            setOnDismissListener {
                viewBinding.imageView.rotate(180.0F, 0.0F, Duration.ROTATION)
            }
        }
    }

    private val popupWindowBinding by lazy { PopupWindowBinding.inflate(layoutInflater) }

    private val elevationPopupWindow by lazy { resources.getDimension(R.dimen.elevation_6dp) }
    private val itemHeight by lazy { resources.getDimensionPixelOffset(R.dimen.height_48dp) }
    private val marginBottom by lazy { resources.getDimensionPixelOffset(R.dimen.height_8dp) }
    private val marginTop by lazy { resources.getDimensionPixelOffset(R.dimen.height_8dp) }

    private var arrayAdapter: ArrayAdapter? = null
    private var onItemClickListener: OnItemClickListener? = null

    private fun getAttrs(attrs: AttributeSet) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DropdownMenu)

        setTypedArray(typedArray)
    }

    private fun getAttrs(attrs: AttributeSet, defStyleAttr: Int) {
        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.DropdownMenu, defStyleAttr, 0)

        setTypedArray(typedArray)
    }

    private fun setTypedArray(typedArray: TypedArray) {
        val rippleDrawable = typedArray.getDrawable(R.styleable.DropdownMenu_rippleDrawable)
        val textColor = typedArray.getColor(R.styleable.DropdownMenu_textColor, context.getColor(R.color.text))
        val tint = typedArray.getColor(R.styleable.DropdownMenu_tint, context.getColor(R.color.icon))

        rippleDrawable?.let {
            viewBinding.linearLayout.background = it
        }

        viewBinding.textView.setTextColor(textColor)
        viewBinding.imageView.setColorFilter(tint)

        typedArray.recycle()
    }

    private fun bind() {
        addView(viewBinding.root)

        viewBinding.root.setOnClickListener {
            showAsDropdown()
        }

        viewBinding.imageView.setOnClickListener {
            showAsDropdown()
        }
    }

    private fun showAsDropdown() {
        if (popupWindow.isShowing.not()){
            popupWindow.contentView = popupWindowBinding.root
            popupWindow.elevation = elevationPopupWindow
            popupWindow.isFocusable = true
            popupWindow.isOutsideTouchable = true
            popupWindow.width = width

            val itemCount = arrayAdapter?.itemCount ?: 0
            val height = if (itemCount < 10) {
                itemCount * itemHeight
            } else {
                itemHeight * 9
            }

            popupWindow.height = height + marginBottom + marginTop

            viewBinding.imageView.rotate(0.0F, 180.0F, Duration.ROTATION)
            popupWindow.showAsDropDown(this)
        }
    }

    fun setAdapter(arrayAdapter: ArrayAdapter, currentItem: Int = 0) {
        this.arrayAdapter = arrayAdapter.apply {
            onItemClickListener?.let { setOnItemClickListener(it) }
        }

        popupWindowBinding.recyclerView.apply {
            adapter = arrayAdapter
            layoutManager = LinearLayoutManagerWrapper(context)
        }

        try {
            viewBinding.textView.text = arrayAdapter.getItem(currentItem)
        } catch (e: IndexOutOfBoundsException) {
            Timber.e(e)
        }
    }

    fun setText(text: String) {
        viewBinding.textView.text = text
    }

    class ArrayAdapter(private val array: Array<String>) : RecyclerView.Adapter<ArrayAdapter.ViewHolder>() {
        private var itemRippleDrawable: Drawable? = null
        private var onItemClickListener: OnItemClickListener? = null

        fun setItemRippleDrawable(itemRippleDrawable: Drawable) {
            this.itemRippleDrawable = itemRippleDrawable
        }

        fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
            this.onItemClickListener = onItemClickListener
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val layoutInflater = LayoutInflater.from(parent.context)

            return ViewHolder(DropdownItemBinding.inflate(layoutInflater, parent, false))
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            holder.bind(position, array[position])
        }

        override fun getItemCount(): Int {
            return array.count()
        }

        fun getItem(position: Int) = array[position]

        inner class ViewHolder(private val viewBinding: DropdownItemBinding) : RecyclerView.ViewHolder(viewBinding.root) {
            fun bind(position: Int, item: String) {
                with(viewBinding.root) {
                    text = item

                    setOnClickListener {
                        onItemClickListener?.onItemClick(position, item)
                    }
                }
            }
        }
    }
}