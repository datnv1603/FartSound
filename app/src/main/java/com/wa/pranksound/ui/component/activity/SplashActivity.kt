package com.wa.pranksound.ui.component.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import com.wa.pranksound.databinding.ActivitySplashBinding
import com.wa.pranksound.utils.BaseActivity

@SuppressLint("CustomSplashScreen")
class SplashActivity : BaseActivity() {
    private lateinit var binding: ActivitySplashBinding
    private val SPLASH_TIME_OUT: Long = 4000 // milliseconds
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashBinding.inflate(layoutInflater)
        setContentView(binding.root)


        handler.postDelayed({
            // Tạo Intent để chuyển sang màn hình mới
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)

            // Đóng Activity hiện tại để không thể quay lại màn hình Splash bằng nút Back
            finish()
        }, SPLASH_TIME_OUT)



    }



    override fun onResume() {
        super.onResume()
        handler.postDelayed({
            // Tạo Intent để chuyển sang màn hình mới
            val intent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(intent)

            // Đóng Activity hiện tại để không thể quay lại màn hình Splash bằng nút Back
            finish()
        }, SPLASH_TIME_OUT)
    }
    override fun onPause() {
        super.onPause()
        handler.removeCallbacksAndMessages(null)
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }
}
