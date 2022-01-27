package com.miso.misoweather.common

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.miso.misoweather.R

open class MisoActivity :AppCompatActivity() {
    val MISOWEATHER_BASE_URL:String = "http://3.35.55.100/"
    lateinit var prefs: SharedPreferences
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        prefs = getSharedPreferences("misoweather", Context.MODE_PRIVATE)

    }
    protected fun transferToBack(){
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }
    protected fun transferToNext(){
        overridePendingTransition(R.anim.slide_right_exit,R.anim.slide_right_enter)
    }
}