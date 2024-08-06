package com.wa.pranksound.ui.adapter

import androidx.recyclerview.widget.DiffUtil
import com.bumptech.glide.Glide
import com.wa.pranksound.R
import com.wa.pranksound.databinding.ItemIntroBinding
import com.wa.pranksound.model.IntroUI
import com.wa.pranksound.ui.base.BaseBindingAdapterDiff

class IntroAdapter : BaseBindingAdapterDiff<IntroUI, ItemIntroBinding>(
	object : DiffUtil.ItemCallback<IntroUI>() {
		override fun areItemsTheSame(oldItem: IntroUI, newItem: IntroUI): Boolean {
			return oldItem.title == newItem.title
		}

		override fun areContentsTheSame(oldItem: IntroUI, newItem: IntroUI): Boolean {
			return oldItem == newItem
		}

	}
) {
	override fun onBindViewHolderBase(holder: BaseHolder<ItemIntroBinding>, position: Int) {
		with(getItem(holder.adapterPosition)) {
			Glide.with(holder.itemView).load(icon).into(holder.binding.imgIntro)
			holder.binding.tvTitle.text = title ?: ""
		}
	}

	override val layoutIdItem: Int
		get() = R.layout.item_intro
}