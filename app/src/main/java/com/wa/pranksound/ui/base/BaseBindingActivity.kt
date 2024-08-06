package com.wa.pranksound.ui.base

import android.annotation.SuppressLint
import android.app.Activity
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.view.Window
import android.widget.Toast
import androidx.core.view.WindowCompat
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.lifecycle.ViewModelProvider
import com.adjust.sdk.Adjust
import com.wa.pranksound.utils.extention.setFullScreen

abstract class BaseBindingActivity<B : ViewDataBinding, VM : BaseViewModel> : BaseActivity() {
    lateinit var binding: B
    lateinit var viewModel: VM
    private var isDispatchTouchEvent = true

    abstract val layoutId: Int
    abstract fun getViewModel(): Class<VM>
    abstract fun setupView(savedInstanceState: Bundle?)
    abstract fun setupData()

    private fun Window.setLightStatusBars(b: Boolean) {
        WindowCompat.getInsetsController(this, decorView).isAppearanceLightStatusBars = b
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        lockPortraitOrientation(this)
        super.onCreate(savedInstanceState)
        setFullScreen()
        binding = DataBindingUtil.setContentView(this, layoutId)
        viewModel = ViewModelProvider(this)[getViewModel()]
        window.setLightStatusBars(false)
        setupView(savedInstanceState)
        setupData()
    }


    override fun onStop() {
        super.onStop()
        toast?.cancel()
    }

    override fun onResume() {
        super.onResume()
        Adjust.onResume()
    }
    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        return !isDispatchTouchEvent || super.dispatchTouchEvent(ev)
    }

    private var toast: Toast? = null

    private var handlerToast = Handler(Looper.getMainLooper())
    fun toast(text: String) {
        toast?.cancel()
        toast = Toast.makeText(this, text, Toast.LENGTH_SHORT)
        toast?.show()

        handlerToast.postDelayed({
            toast?.cancel()
        }, 1500)
    }


    override fun onDestroy() {
        super.onDestroy()
        handlerToast.removeCallbacksAndMessages(null)
    }


    fun isDispatchTouchEvent(time: Int) {
        isDispatchTouchEvent = false
        Handler(Looper.getMainLooper()).postDelayed({ isDispatchTouchEvent = true }, time.toLong())
    }

    @SuppressLint("SourceLockedOrientationActivity")
    private fun lockPortraitOrientation(activity: Activity) {
        if (isSamsungDeviceBelowAndroid10()) {
            activity.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    private fun isSamsungDeviceBelowAndroid10(): Boolean {
        val manufacturer = Build.MANUFACTURER
        try {
            if (manufacturer.equals("Samsung", ignoreCase = true) && Build.VERSION.SDK_INT < 29) {
                return true
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        }
        return false
    }

}