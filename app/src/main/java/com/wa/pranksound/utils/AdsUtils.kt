package com.wa.pranksound.utils

import android.app.Activity
import android.content.Context
import android.view.View
import android.view.ViewGroup
import com.dungvnhh98.percas.studio.admoblib.ViewControl.gone
import com.dungvnhh98.percas.studio.admoblib.admob.AdmobManager
import com.dungvnhh98.percas.studio.admoblib.model.InterAdHolder
import com.google.android.gms.ads.AdValue

object AdsUtils {
    var ADS_BANNER = Ad_ID.ADS_BANNER
    var ADS_OPEN = Ad_ID.ADS_OPEN

    var interAdHolder = InterAdHolder(Ad_ID.ADS_INTER.toString())

    var lastTimeShowInter : Long = 0L
    var timeInter : Int = RemoteConfig().timeDelayInter()

    fun loadAndShowInterstitialAd(activity: Activity, interAdHolder: InterAdHolder, callBack: loadAndShow){
        val currentTime = System.currentTimeMillis()
        if(currentTime - lastTimeShowInter >= timeInter){
            AdmobManager.loadAndShowInterstitialAd(activity, interAdHolder, object :AdmobManager.LoadAndShowAdCallBack{
                override fun onAdLoaded() {

                }

                override fun onAdShowed() {

                }

                override fun onAdFailed(error: String) {
                    callBack.onAdFailed()
                }

                override fun onAdClosed() {
                    lastTimeShowInter = currentTime
                    callBack.onAdClose()
                }

                override fun onAdClicked() {

                }

                override fun onAdPaid(adValue: AdValue, adUnit: String) {
                    AdjustManager.postRevenue(adValue, adUnit)
                }

            })
        }else{
            callBack.onAdFailed()
        }

    }

    interface loadAndShow{
        fun onAdClose()
        fun onAdFailed()
    }
    fun loadAndShowBannerCollapsibleAd(activity: Activity, idBannerCollapsible: String, isBottomCollapsible:Boolean, viewBannerCollapsibleAd: ViewGroup, viewLine: View) {
        AdmobManager.loadAndShowBannerCollapsibleAd(
            activity,
            idBannerCollapsible,
            isBottomCollapsible,
            viewBannerCollapsibleAd,
            object : AdmobManager.LoadAndShowAdCallBack {
                override fun onAdLoaded() {
                }

                override fun onAdShowed() {

                }

                override fun onAdFailed(error: String) {
                    viewBannerCollapsibleAd.gone()
                    viewLine.gone()
                }

                override fun onAdClosed() {

                }

                override fun onAdClicked() {

                }

                override fun onAdPaid(adValue: AdValue, adUnit: String) {
                    AdjustManager.postRevenue(adValue, adUnit)
                }

            })
    }

    fun loadAndShowBanner(
        activity: Activity,
        idBannerAd: String,
        viewBannerAd: ViewGroup,
        viewLine: View
    ) {
        AdmobManager.loadAndShowBannerAd(
            activity,
            idBannerAd,
            viewBannerAd,
            object : AdmobManager.LoadAndShowAdCallBack {
                override fun onAdLoaded() {}

                override fun onAdShowed() {

                }

                override fun onAdFailed(error: String) {
                    viewBannerAd.gone()
                    viewLine.gone()
                }

                override fun onAdClosed() {}

                override fun onAdClicked() {}

                override fun onAdPaid(adValue: AdValue, adUnit: String) {
                    AdjustManager.postRevenue(adValue, adUnit)
                }

            })
    }

    fun loadInterstitialAd(context: Context, interAdHolder: InterAdHolder){
        AdmobManager.loadInterstitialAd(context, interAdHolder, object : AdmobManager.LoadAdCallBack{
            override fun onAdLoaded() {
            }

            override fun onAdFailed(error: String) {
            }

            override fun onAdClicked() {
            }

            override fun onAdPaid(adValue: AdValue, adUnit: String) {
                AdjustManager.postRevenue(adValue, adUnit)
            }
        })
    }

    fun RemoteBanner(activity: Activity, idBannerCollapsible: String, isBottomCollapsible:Boolean, viewBannerCollapsibleAd: ViewGroup, viewLine: View){
        if (RemoteConfig().isEnableBanner()){
            loadAndShowBannerCollapsibleAd(activity,idBannerCollapsible,isBottomCollapsible,viewBannerCollapsibleAd,viewLine)
        }else{
            loadAndShowBanner(activity,idBannerCollapsible,viewBannerCollapsibleAd,viewLine)
        }
    }


}