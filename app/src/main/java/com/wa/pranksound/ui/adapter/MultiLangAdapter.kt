package com.wa.pranksound.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.wa.pranksound.R
import com.wa.pranksound.databinding.ItemMultiLangBinding
import com.wa.pranksound.model.LanguageUI
import com.wa.pranksound.ui.base.BaseBindingAdapterDiff
import com.wa.pranksound.utils.extention.setOnSafeClick

class MultiLangAdapter : BaseBindingAdapterDiff<LanguageUI, ItemMultiLangBinding>(
    object : DiffUtil.ItemCallback<LanguageUI>() {
        override fun areItemsTheSame(oldItem: LanguageUI, newItem: LanguageUI): Boolean {
            return oldItem.code == newItem.code
        }

        override fun areContentsTheSame(oldItem: LanguageUI, newItem: LanguageUI): Boolean {
            return oldItem == newItem
        }
    }
) {

    private var oldPosition: Int = -1
        set(value) {
            field = value
            notifyItemChanged(value)
        }

    var newPosition: Int = -1
        set(value) {
            oldPosition = field
            field = value
            notifyItemChanged(value)
        }

    fun getCurrentLanguage() = currentList[newPosition]
    var callBack: (Int, LanguageUI) -> Unit = { _, _ -> }
    override fun onBindViewHolderBase(holder: BaseHolder<ItemMultiLangBinding>, position: Int) {
        with(getItem(holder.adapterPosition)) {
            holder.binding.apply {
                tvLanguage.text = name
                if (holder.adapterPosition == newPosition) {
                    avatar?.let {
                        llItemLanguage.isSelected = true
                        radioBtn.isChecked = true
                        imgLanguage.setImageResource(it)
                    }

                } else {
                    avatar?.let {
                        llItemLanguage.isSelected = false
                        radioBtn.isChecked = false
                        imgLanguage.setImageResource(it)
                    }
                }
                root.setOnSafeClick {
                    callBack(position, this@with)
                    newPosition = holder.adapterPosition
                }
            }
            setAnimation(holder.itemView, holder.adapterPosition, holder.itemView.context)

        }
    }

    override val layoutIdItem: Int
        get() = R.layout.item_multi_lang
}