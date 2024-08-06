package com.wa.pranksound.ui.component.intro

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.wa.pranksound.R
import com.wa.pranksound.model.IntroUI
import com.wa.pranksound.ui.base.BaseViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch

class IntroViewModel : BaseViewModel() {
    private val _introMutableLiveData: MutableLiveData<List<IntroUI>> = MutableLiveData()
    val introMutableLiveData: LiveData<List<IntroUI>>
        get() = _introMutableLiveData

    override fun onCleared() {
        super.onCleared()
        viewModelScope.cancel()
    }
    
    fun getIntro(context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            val listIntroEntity = mutableListOf<IntroUI>()
            val titlesResourceIds = listOf(
                R.string.title_intro_1,
                R.string.title_intro_2,
                R.string.title_intro_3,
                )

            val drawableResourceIds = listOf(
                R.drawable.intro1,
                R.drawable.intro2,
                R.drawable.intro3,
            )

            for (i in titlesResourceIds.indices) {
                listIntroEntity.add(
                    IntroUI(
                        context.getString(titlesResourceIds[i]),
                        drawableResourceIds[i]
                    )
                )
            }
            _introMutableLiveData.postValue(listIntroEntity)
        }
    }
}