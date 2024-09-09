package com.wa.pranksound.utils.ads

import android.app.Activity
import android.content.res.Resources
import android.os.Bundle
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.widget.FrameLayout
import com.adjust.sdk.Adjust
import com.adjust.sdk.AdjustAdRevenue
import com.adjust.sdk.AdjustConfig
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.ads.mediation.admob.AdMobAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdValue
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.AdapterResponseInfo
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnPaidEventListener
import com.google.firebase.analytics.FirebaseAnalytics
import com.wa.pranksound.R
import com.wa.pranksound.utils.Utils
import com.wa.pranksound.utils.extention.gone
import com.wa.pranksound.utils.extention.visible

class BannerUtils {
    private val BANNER_INLINE_SMALL_STYLE = "BANNER_INLINE_SMALL_STYLE"
    private val BANNER_INLINE_LARGE_STYLE = "BANNER_INLINE_LARGE_STYLE"
    private val MAX_SMALL_INLINE_BANNER_HEIGHT = 50
    private lateinit var analytics: FirebaseAnalytics
    private var retryAttempt = 0.0

    var loadNativeCount = 0

    companion object {

        var instance: BannerUtils? = null
            get() {
                if (field == null) field = BannerUtils()
                return field
            }
    }

    //Load banner in activity
    fun loadBanner(mActivity: Activity, id: String, adsLoadCallBack: (Boolean) -> Unit) {
        val adContainer = mActivity.findViewById<FrameLayout>(R.id.banner_container)
        val containerShimmer =
            mActivity.findViewById<ShimmerFrameLayout>(R.id.shimmer_container_banner)
        if (!Utils.checkInternetConnection(mActivity)) {
            adContainer.gone()
            containerShimmer.gone()
            adsLoadCallBack(false)
        } else {
            loadBanner(
                mActivity,
                id,
                adContainer,
                containerShimmer,
                false, BANNER_INLINE_LARGE_STYLE, adsLoadCallBack
            )
        }
    }

    private fun loadBanner(
        mActivity: Activity,
        id: String,
        adContainer: FrameLayout,
        containerShimmer: ShimmerFrameLayout,
        useInlineAdaptive: Boolean,
        inlineStyle: String,
        adsLoadCallBack: (Boolean) -> Unit,
    ) {
        containerShimmer.visible()
        containerShimmer.startShimmer()
        try {
            val adView = AdView(mActivity)
            adView.adUnitId = id
            adContainer.addView(adView)
            val adSize: AdSize = getAdSize(mActivity, useInlineAdaptive, inlineStyle)
            val adHeight: Int = if (useInlineAdaptive && inlineStyle.equals(
                    BANNER_INLINE_SMALL_STYLE,
                    ignoreCase = true
                )
            ) {
                MAX_SMALL_INLINE_BANNER_HEIGHT
            } else {
                adSize.height
            }
            containerShimmer.layoutParams.height =
                (adHeight * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
            adView.setAdSize(adSize)
            adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            adView.adListener = object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    containerShimmer.stopShimmer()
                    adContainer.gone()
                    containerShimmer.gone()
                    adsLoadCallBack(false)
                }

                override fun onAdLoaded() {
                    containerShimmer.stopShimmer()
                    containerShimmer.gone()
                    adContainer.visible()
                    adsLoadCallBack(true)
                    adView.onPaidEventListener = OnPaidEventListener { adValue: AdValue ->
                        val loadedAdapterResponseInfo: AdapterResponseInfo? =
                            adView.responseInfo?.loadedAdapterResponseInfo
                        val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                        val revenue = adValue.valueMicros.toDouble() / 1000000.0
                        adRevenue.setRevenue(
                            revenue,
                            adValue.currencyCode
                        )
                        adRevenue.setAdRevenueNetwork(loadedAdapterResponseInfo?.adSourceName)
                        Adjust.trackAdRevenue(adRevenue)
                        analytics = FirebaseAnalytics.getInstance(mActivity)
                        val params = Bundle()
                        params.putString(
                            FirebaseAnalytics.Param.AD_PLATFORM,
                            loadedAdapterResponseInfo?.adSourceName
                        )
                        params.putString(FirebaseAnalytics.Param.AD_SOURCE, "AdMob")
                        params.putString(FirebaseAnalytics.Param.AD_FORMAT, "Banner")
                        params.putDouble(FirebaseAnalytics.Param.VALUE, revenue)
                        params.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                        analytics.logEvent("ad_impression_2", params)
                    }
                }

                override fun onAdClicked() {
                    super.onAdClicked()
                }

                override fun onAdImpression() {
                    super.onAdImpression()
                }

                override fun onAdClosed() {
                    super.onAdClosed()
                }

                override fun onAdOpened() {
                    super.onAdOpened()
                }
            }
            adView.loadAd(getAdsRequest())
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    //Load CollapsibleBanner in activity
    fun loadCollapsibleBanner(mActivity: Activity, id: String, adsLoadCallBack: (Boolean) -> Unit) {
        val adContainer = mActivity.findViewById<FrameLayout>(R.id.banner_container)
        val containerShimmer =
            mActivity.findViewById<ShimmerFrameLayout>(R.id.shimmer_container_banner)
        if (!Utils.checkInternetConnection(mActivity)) {
            adContainer.gone()
            containerShimmer.gone()
            adsLoadCallBack(false)
        } else {
            loadCollapsibleBanner(
                mActivity,
                id,
                adContainer,
                containerShimmer,
                false, BANNER_INLINE_LARGE_STYLE,
                adsLoadCallBack
            )
        }
    }

    private fun loadCollapsibleBanner(
        mActivity: Activity,
        id: String,
        adContainer: FrameLayout,
        containerShimmer: ShimmerFrameLayout,
        useInlineAdaptive: Boolean,
        inlineStyle: String,
        adsLoadCallBack: (Boolean) -> Unit,
    ) {
        containerShimmer.visible()
        containerShimmer.startShimmer()
        try {
            val adView = AdView(mActivity)
            adView.adUnitId = id
            adContainer.addView(adView)
            val adSize: AdSize = getAdSize(mActivity, useInlineAdaptive, inlineStyle)
            val adHeight: Int = if (useInlineAdaptive && inlineStyle.equals(
                    BANNER_INLINE_SMALL_STYLE,
                    ignoreCase = true
                )
            ) {
                MAX_SMALL_INLINE_BANNER_HEIGHT
            } else {
                adSize.height
            }
            containerShimmer.layoutParams.height =
                (adHeight * Resources.getSystem().displayMetrics.density + 0.5f).toInt()
            adView.setAdSize(adSize)

            val extras = Bundle()
            extras.putString("collapsible", "bottom")

            val adRequest = AdRequest.Builder()
                .addNetworkExtrasBundle(AdMobAdapter::class.java, extras)
                .build()

            adView.setLayerType(View.LAYER_TYPE_SOFTWARE, null)
            adView.adListener = object : AdListener() {
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    containerShimmer.stopShimmer()
                    adContainer.gone()
                    containerShimmer.gone()
                    adsLoadCallBack(false)
                    Log.d("datnv", "onAdFailedToLoad: ${loadAdError.message}")
                    Log.d("datnv", "onAdFailedToLoad: ${loadAdError.responseInfo}")
                    Log.d("datnv", "onAdFailedToLoad: ${loadAdError.cause}")
                }

                override fun onAdLoaded() {
                    containerShimmer.stopShimmer()
                    containerShimmer.gone()
                    adContainer.visible()
                    adsLoadCallBack(true)
                    adView.onPaidEventListener = OnPaidEventListener { adValue: AdValue ->
                        val loadedAdapterResponseInfo: AdapterResponseInfo? =
                            adView.responseInfo?.loadedAdapterResponseInfo
                        val adRevenue = AdjustAdRevenue(AdjustConfig.AD_REVENUE_ADMOB)
                        val revenue = adValue.valueMicros.toDouble() / 1000000.0
                        adRevenue.setRevenue(
                            revenue,
                            adValue.currencyCode
                        )
                        adRevenue.adRevenueNetwork = loadedAdapterResponseInfo?.adSourceName
                        Adjust.trackAdRevenue(adRevenue)
                        analytics = FirebaseAnalytics.getInstance(mActivity)
                        val params = Bundle()
                        params.putString(
                            FirebaseAnalytics.Param.AD_PLATFORM,
                            loadedAdapterResponseInfo?.adSourceName
                        )
                        params.putString(FirebaseAnalytics.Param.AD_SOURCE, "AdMob")
                        params.putString(FirebaseAnalytics.Param.AD_FORMAT, "Banner")
                        params.putDouble(FirebaseAnalytics.Param.VALUE, revenue )
                        params.putString(FirebaseAnalytics.Param.CURRENCY, "USD")
                        analytics.logEvent("ad_impression_2", params)
                    }
                }
            }
            adView.loadAd(adRequest)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getAdSize(
        mActivity: Activity,
        useInlineAdaptive: Boolean,
        inlineStyle: String,
    ): AdSize {

        // Step 2 - Determine the screen width (less decorations) to use for the ad width.
        val display = mActivity.windowManager.defaultDisplay
        val outMetrics = DisplayMetrics()
        display.getMetrics(outMetrics)
        val widthPixels = outMetrics.widthPixels.toFloat()
        val density = outMetrics.density
        val adWidth = (widthPixels / density).toInt()

        // Step 3 - Get adaptive ad size and return for setting on the ad view.
        return if (useInlineAdaptive) {
            if (inlineStyle.equals(BANNER_INLINE_LARGE_STYLE, ignoreCase = true)) {
                AdSize.getCurrentOrientationInlineAdaptiveBannerAdSize(mActivity, adWidth)
            } else {
                AdSize.getInlineAdaptiveBannerAdSize(adWidth, MAX_SMALL_INLINE_BANNER_HEIGHT)
            }
        } else AdSize.getCurrentOrientationAnchoredAdaptiveBannerAdSize(mActivity, adWidth)
    }
}