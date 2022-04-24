package com.miso.misoweather.activity.answerAnimationActivity

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.*
import androidx.annotation.RequiresApi
import com.miso.misoweather.activity.chatmain.ChatMainActivity
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityAnimationMyanswerBinding

@RequiresApi(Build.VERSION_CODES.O)
class AnswerAnimationActivity : MisoActivity() {
    private lateinit var binding: ActivityAnimationMyanswerBinding
    private lateinit var txt_answer: TextView
    private lateinit var img_animation: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityAnimationMyanswerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
        Handler().postDelayed({
            goToChatMainActivity()
        }, 1200)
    }

    private fun initializeViews() {
        txt_answer = binding.txtAnswer
        txt_answer.text = intent.getStringExtra("answer")!!
        img_animation = binding.imgCheckAnimation
    }

    override fun doBack() {
    }

    private fun goToChatMainActivity() {
        startActivity(Intent(this, ChatMainActivity::class.java))
        sinkFromTop()
        finish()
    }
}