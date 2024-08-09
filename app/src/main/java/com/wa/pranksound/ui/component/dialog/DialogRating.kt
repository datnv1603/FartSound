package com.wa.pranksound.ui.component.dialog

import android.content.Context
import android.os.Bundle
import android.view.View
import com.wa.pranksound.R
import com.wa.pranksound.common.Constant
import com.wa.pranksound.databinding.DialogRatingBinding
import com.wa.pranksound.ui.base.BaseBindingDialogFragment
import com.wa.pranksound.utils.extention.setOnSafeClick
import com.wa.pranksound.utils.extention.visible

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
		val sharePref = requireContext().getSharedPreferences("PREFERENCE", Context.MODE_PRIVATE)
		sharePref.edit().putBoolean(Constant.IS_RATED, true).apply()
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
}