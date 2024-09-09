package com.joya.pranksound.ui.component.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.widget.TextView
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentManager
import com.joya.pranksound.R
import com.joya.pranksound.databinding.ActivityMainBinding
import com.joya.pranksound.ui.base.BaseBindingActivity
import com.joya.pranksound.ui.component.fragment.CreateFragment
import com.joya.pranksound.ui.component.fragment.FavoritesFragment
import com.joya.pranksound.ui.component.fragment.HomeFragment
import com.joya.pranksound.ui.component.fragment.LeaderBoardFragment
import com.joya.pranksound.ui.component.settings.SettingsActivity
import com.joya.pranksound.utils.Gdpr
import com.joya.pranksound.utils.extention.setOnSafeClick

class MainActivity : BaseBindingActivity<ActivityMainBinding, MainViewModel>() {
    private var fragmentManager: FragmentManager? = null
    private var navTab: String = "home"

    override val layoutId: Int
        get() = R.layout.activity_main

    override fun getViewModel(): Class<MainViewModel> = MainViewModel::class.java

    override fun setupData() {
    }

    override fun setupView(savedInstanceState: Bundle?) {
        findView()

        fragmentManager = supportFragmentManager
        bottomNavigationClick()
        val getRecord = intent.getStringExtra("from_record")

        if (getRecord != null) {
            changeFragment("create")
        } else {
            changeFragment("home")
        }

        binding.btnSettings.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    SettingsActivity::class.java
                )
            )
        }


    }

    private fun changeFragment(tab: String) {
        when (tab) {
            "home" -> {
                fragmentManager!!.beginTransaction().replace(R.id.fragmentMain, HomeFragment())
                    .commit()
                binding.tvTitle.setText(R.string.home)

                binding.imgHome.setBackgroundResource(R.drawable.ic_home_selected)
                binding.tvHome.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color_selected
                    )
                )

                binding.imgCreate.setBackgroundResource(R.drawable.ic_create_unselected)
                binding.tvCreate.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgFavorites.setBackgroundResource(R.drawable.ic_fav_unselected)
                binding.tvFavorites.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgLeaderboard.setBackgroundResource(R.drawable.ic_leader_board_unselected)
                binding.tvLeaderboard.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )
            }

            "create" -> {
                fragmentManager!!.beginTransaction().replace(R.id.fragmentMain, CreateFragment())
                    .commit()
                binding.tvTitle.setText(R.string.create_sound)

                binding.imgHome.setBackgroundResource(R.drawable.ic_home_unselected)
                binding.tvHome.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgCreate.setBackgroundResource(R.drawable.ic_create_selected)
                binding.tvCreate.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color_selected
                    )
                )

                binding.imgFavorites.setBackgroundResource(R.drawable.ic_fav_unselected)
                binding.tvFavorites.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgLeaderboard.setBackgroundResource(R.drawable.ic_leader_board_unselected)
                binding.tvLeaderboard.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )
            }

            "favorites" -> {
                fragmentManager!!.beginTransaction().replace(R.id.fragmentMain, FavoritesFragment())
                    .commit()
                binding.tvTitle.setText(R.string.favorites)

                binding.imgHome.setBackgroundResource(R.drawable.ic_home_unselected)
                binding.tvHome.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgCreate.setBackgroundResource(R.drawable.ic_create_unselected)
                binding.tvCreate.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgFavorites.setBackgroundResource(R.drawable.ic_fav_selected)
                binding.tvFavorites.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color_selected
                    )
                )

                binding.imgLeaderboard.setBackgroundResource(R.drawable.ic_leader_board_unselected)
                binding.tvLeaderboard.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )
            }

            "leaderboard" -> {
                fragmentManager!!.beginTransaction()
                    .replace(R.id.fragmentMain, LeaderBoardFragment()).commit()
                binding.tvTitle.setText(R.string.leaderboard)

                binding.imgHome.setBackgroundResource(R.drawable.ic_home_unselected)
                binding.tvHome.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgCreate.setBackgroundResource(R.drawable.ic_create_unselected)
                binding.tvCreate.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgFavorites.setBackgroundResource(R.drawable.ic_fav_unselected)
                binding.tvFavorites.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgLeaderboard.setBackgroundResource(R.drawable.ic_leader_board_selected)
                binding.tvLeaderboard.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color_selected
                    )
                )
            }

            else -> {
                fragmentManager!!.beginTransaction().replace(R.id.fragmentMain, HomeFragment())
                    .commit()
                binding.tvTitle.setText(R.string.home)

                binding.imgHome.setBackgroundResource(R.drawable.ic_home_selected)
                binding.tvHome.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color_selected
                    )
                )

                binding.imgCreate.setBackgroundResource(R.drawable.ic_create_unselected)
                binding.tvCreate.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgFavorites.setBackgroundResource(R.drawable.ic_fav_unselected)
                binding.tvFavorites.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )

                binding.imgLeaderboard.setBackgroundResource(R.drawable.ic_leader_board_unselected)
                binding.tvLeaderboard.setTextColor(
                    ContextCompat.getColor(
                        this,
                        R.color.bottom_nav_text_color
                    )
                )
            }
        }
    }

    private fun bottomNavigationClick() {
        binding.apply {
            llHome.setOnSafeClick {
                navTab = "home"
                changeFragment(navTab)
            }

            llCreate.setOnSafeClick {
                navTab = "create"
                changeFragment(navTab)
            }

            llFavorites.setOnSafeClick {
                navTab = "favorites"
                changeFragment(navTab)
            }

            llLeaderboard.setOnSafeClick {
                navTab = "leaderboard"
                changeFragment(navTab)
            }
        }
    }

    private fun findView() {
        Gdpr().make(this)
        onBackPressedDispatcher.addCallback(object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                showExitDialog()
            }
        })
    }

    fun showExitDialog() {
        val dialogCustomExit = Dialog(this@MainActivity)
        dialogCustomExit.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialogCustomExit.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        val lp = WindowManager.LayoutParams()
        lp.copyFrom(dialogCustomExit.window!!.attributes)
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.MATCH_PARENT
        dialogCustomExit.setContentView(R.layout.exit_dialog_layout)
        dialogCustomExit.setCancelable(true)
        dialogCustomExit.show()
        dialogCustomExit.window!!.attributes = lp

        val btnNegative = dialogCustomExit.findViewById<TextView>(R.id.btnNegative)
        val btnPositive = dialogCustomExit.findViewById<TextView>(R.id.btnPositive)

        btnPositive.setOnSafeClick {
            dialogCustomExit.dismiss()
            finishAffinity()
        }
        btnNegative.setOnSafeClick { dialogCustomExit.dismiss() }
    }
}