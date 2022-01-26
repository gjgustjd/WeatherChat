package com.miso.misoweather.common

import androidx.appcompat.app.AppCompatActivity
import com.miso.misoweather.R

open class MisoActivity :AppCompatActivity() {
    protected fun transferToBack(){
        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit)
    }
    protected fun transferToNext(){
        overridePendingTransition(R.anim.slide_right_exit,R.anim.slide_right_enter)
    }
}