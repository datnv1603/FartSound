package com.joya.pranksound.ui.base

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.recyclerview.widget.RecyclerView
abstract class BaseBindingAdapter<B : ViewDataBinding> : RecyclerView.Adapter<BaseBindingAdapter.BaseHolder<B>>() {

    protected abstract fun onBindViewHolderBase(holder: BaseHolder<B>, position: Int)

    protected abstract val layoutIdItem: Int
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BaseHolder<B> {
        val binding = DataBindingUtil.inflate<B>(
            LayoutInflater.from(parent.context),
            layoutIdItem,
            parent,
            false
        )
        return BaseHolder(binding)
    }

    override fun onBindViewHolder(holder: BaseHolder<B>, position: Int) {
        onBindViewHolderBase(holder, holder.adapterPosition)
    }

    class BaseHolder<B : ViewDataBinding>(var binding: B) : RecyclerView.ViewHolder(
        binding.root
    )

    private var lastPosition = -1
    protected fun setAnimation(viewToAnimate: View, position: Int, context: Context) {
        // If the bound view wasn't previously displayed on screen, it's animated
        if (position > lastPosition) {
            val animation: Animation =
                AnimationUtils.loadAnimation(context, android.R.anim.slide_in_left)
            viewToAnimate.startAnimation(animation)
            lastPosition = position
        }
    }


}