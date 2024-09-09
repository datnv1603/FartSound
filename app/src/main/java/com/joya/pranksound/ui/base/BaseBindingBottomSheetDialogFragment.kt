package com.joya.pranksound.ui.base

import android.annotation.SuppressLint
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import com.joya.pranksound.R

abstract class BaseBindingBottomSheetDialogFragment<B : ViewDataBinding> :
    BaseBottomSheetDialogFragment() {

    lateinit var binding: B
    abstract val layoutId: Int


    private var toast: Toast? = null
    private var handlerToast = Handler(Looper.getMainLooper())

    @SuppressLint("ShowToast")
    fun toast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(context, text, Toast.LENGTH_SHORT)
        toast?.show()
        handlerToast.postDelayed({
            toast?.cancel()
        }, 1500)
    }

    override fun onDestroy() {
        super.onDestroy()
        handlerToast.removeCallbacksAndMessages(null)

    }
    protected abstract fun onCreatedView(view: View?, savedInstanceState: Bundle?)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = DataBindingUtil.inflate(inflater, layoutId, container, false)
        return binding.root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.CustomBottomSheetDialogTheme)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onCreatedView(view, savedInstanceState)
    }
}