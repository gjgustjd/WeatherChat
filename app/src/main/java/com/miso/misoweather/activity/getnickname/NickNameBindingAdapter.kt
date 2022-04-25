package com.miso.misoweather.activity.getnickname

import android.content.Context
import android.graphics.Paint
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.miso.misoweather.R
import kotlin.contracts.contract

object NickNameBindingAdapter {
    @BindingAdapter("greetingText")
    @JvmStatic
    fun setGreetingText(view: TextView, greetingText: String) {
        greetingText?.let {
            view.text = greetingText
        }
    }

    @BindingAdapter("nicknameEmoji")
    @JvmStatic
    fun setEmoji(view: TextView, emoji: String) {
        emoji?.let {
            view.text = it
        }
    }

    @BindingAdapter("underline")
    @JvmStatic
    fun setUnderline(view: TextView, isUnderline: Boolean) {
        if (isUnderline)
            view.paintFlags = Paint.UNDERLINE_TEXT_FLAG
    }
}