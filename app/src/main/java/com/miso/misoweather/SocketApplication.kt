package com.miso.misoweather

import android.app.Application
import android.content.Context
import androidx.multidex.MultiDexApplication
import com.kakao.sdk.common.KakaoSdk
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class SocketApplication : MultiDexApplication() {
    override fun onCreate() {
        super.onCreate()
        KakaoSdk.init(applicationContext,getString(R.string.kakao_app_key))
    }
}
