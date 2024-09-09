package com.joya.pranksound.ui.base

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.joya.pranksound.utils.SystemUtil

abstract class BaseActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        SystemUtil.setLocale(this)
        super.onCreate(savedInstanceState)
    }

    override fun attachBaseContext(newBase: Context) {
        SystemUtil.setLocale(newBase)
        super.attachBaseContext(newBase)
    }
}