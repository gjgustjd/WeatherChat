package com.miso.misoweather.Acitivity.selectAnswer

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.chatmain.ChatMainActivity
import com.miso.misoweather.Acitivity.chatmain.SurveyItem
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySurveyAnswerBinding

class SelectSurveyAnswerActivity : MisoActivity() {
    lateinit var binding: ActivitySurveyAnswerBinding
    lateinit var btn_back: ImageButton
    lateinit var btn_action: Button
    lateinit var surveyItem:SurveyItem
    lateinit var recycler_answers:RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySurveyAnswerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
    }

    fun initializeViews() {
        surveyItem = intent.getSerializableExtra("SurveyItem") as SurveyItem
        btn_back = binding.imgbtnBack
        btn_back.setOnClickListener()
        {
            var intent = Intent(this,ChatMainActivity::class.java)
            intent.putExtra("previousActivity","Home")
            startActivity(intent)
            transferToBack()
            finish()
        }
        btn_action = binding.btnAction
        btn_action.setOnClickListener()
        {

        }
        recycler_answers = binding.recyclerAnswers
    }
}