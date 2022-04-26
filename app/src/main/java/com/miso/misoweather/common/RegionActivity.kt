package com.miso.misoweather.common

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.MutableLiveData
import com.miso.misoweather.R

open class RegionActivity : MisoActivity() {
    val recyclerData = MutableLiveData<List<Any>>()
    var bigScale = ""
    var midScale = ""
    var smallScale = ""

    override fun onBackPressed() {
        doBack()
    }

    open fun goToNextActivity() {
    }
}