package com.joya.pranksound.ui.component.dialog

import android.os.Bundle
import android.view.View
import com.adjust.sdk.Adjust
import com.joya.pranksound.R
import com.joya.pranksound.common.Constant
import com.joya.pranksound.data.SharedPreferenceHelper
import com.joya.pranksound.databinding.DialogRatingBinding
import com.joya.pranksound.ui.base.BaseBindingDialogFragment
import com.joya.pranksound.utils.extention.setOnSafeClick
import com.joya.pranksound.utils.extention.visible

class DialogRating : BaseBindingDialogFragment<DialogRatingBinding>() {

	var onClickFiveStar: (() -> Unit) = {}
	var onRating: (() -> Unit) = {}

	var onDismiss: () -> Unit = {}

	override val layoutId: Int
		get() = R.layout.dialog_rating

	override fun onCreatedView(view: View?, savedInstanceState: Bundle?) {
		onclick()
		changeRating()
		binding.btnRate.text = context?.getString(R.string.rate)
		binding.imgStateRate.setImageResource(R.drawable.img_rating_default)
		binding.rbRate.rating = 5f
	}

	private fun savePrefData() {
		SharedPreferenceHelper.storeBoolean(Constant.KEY_IS_RATE, true)
	}

	private fun changeRating() {
		binding.rbRate.setOnRatingChangeListener { _, rating, _ ->
			when (rating.toString()) {
				"1.0" -> {
					binding.tvTitle.text = context?.getString(R.string.title_dialog_rating_1)
					binding.tvContent.text =
						context?.getString(R.string.content_dialog_rating_1_2_3)
					binding.imgStateRate.setImageResource(R.drawable.img_rating_1)
				}

				"2.0" -> {
					binding.tvTitle.text = context?.getString(R.string.title_dialog_rating_1)
					binding.tvContent.text =
						context?.getString(R.string.content_dialog_rating_1_2_3)
					binding.imgStateRate.setImageResource(R.drawable.img_rating_2)
				}

				"3.0" -> {
					binding.tvTitle.text = context?.getString(R.string.title_dialog_rating_1)
					binding.tvContent.text =
						context?.getString(R.string.content_dialog_rating_1_2_3)
					binding.imgStateRate.setImageResource(R.drawable.img_rating_3)
				}

				"4.0" -> {
					binding.tvTitle.text = context?.getString(R.string.title_dialog_rating_5)
					binding.tvContent.text = context?.getString(R.string.content_dialog_rating_4_5)
					binding.imgStateRate.setImageResource(R.drawable.img_rating_4)
				}

				"5.0" -> {
					binding.tvTitle.text = context?.getString(R.string.title_dialog_rating_5)
					binding.tvContent.text = context?.getString(R.string.content_dialog_rating_4_5)
					binding.imgStateRate.setImageResource(R.drawable.img_rating_5)
				}

				else -> {
					binding.tvTitle.text = context?.getString(R.string.title_rate_default)
					binding.tvContent.text =
						context?.getString(R.string.content_rate_default)
					binding.imgStateRate.setImageResource(R.drawable.img_rating_default)
				}
			}
		}
	}


	private fun onclick() {
		binding.btnRate.setOnSafeClick {
			if (binding.rbRate.rating == 0f) {
				toast(getString(R.string.please_give_us_some_feedback))
			} else {
				binding.imgStateRate.visible()
				if (binding.rbRate.rating == 5f) {
					savePrefData()
					toast(getString(R.string.thank_you))
					onClickFiveStar()
				} else {
					toast(getString(R.string.thank_you))
					onRating()
				}
			}
		}
		binding.tvExit.setOnSafeClick {
			dismiss()
		}
	}

	override fun onResume() {
		super.onResume()

		Adjust.onResume()
	}

	override fun onPause() {
		super.onPause()

		Adjust.onPause()
	}
}