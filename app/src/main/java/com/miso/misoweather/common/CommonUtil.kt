package com.miso.misoweather.common

import android.util.Log
import java.lang.Exception

object CommonUtil {
    fun toIntString(text: String): String {
        if (text.equals(""))
            return "0"
        else {
            try {
                var numericString = text.replace("[^0-9.]".toRegex(), "")
                var intNumber = numericString.toFloat().toInt()
                return intNumber.toString()
            }catch (e:Exception)
            {
                Log.e("toIntString",e.stackTraceToString())
                return "0"
            }
        }
    }
}