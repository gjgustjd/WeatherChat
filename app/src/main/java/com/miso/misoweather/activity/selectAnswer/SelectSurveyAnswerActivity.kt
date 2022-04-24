package com.miso.misoweather.activity.selectAnswer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.activity.answerAnimationActivity.AnswerAnimationActivity
import com.miso.misoweather.activity.chatmain.ChatMainActivity
import com.miso.misoweather.activity.chatmain.SurveyItem
import com.miso.misoweather.activity.home.HomeActivity
import com.miso.misoweather.activity.weatherdetail.WeatherDetailActivity
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySurveyAnswerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.launch
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class SelectSurveyAnswerActivity : MisoActivity() {
    private val viewModel: SelectAnswerViewModel by viewModels()
    private lateinit var binding: ActivitySurveyAnswerBinding
    private lateinit var btn_back: ImageButton
    private lateinit var btn_action: Button
    private lateinit var txtQuestion: TextView
    private lateinit var surveyItem: SurveyItem
    private lateinit var recycler_answers: RecyclerView
    private lateinit var recyclerAdapter: RecyclerSurveyAnswersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySurveyAnswerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkAndInitializeViews()
    }

    private fun checkAndInitializeViews() {
        if (intent.getSerializableExtra("SurveyItem") == null) {
            val questions = resources.getStringArray(R.array.survey_questions)
            val randomIndex = Random.nextInt(questions.size)
            getSurveyAnswer(randomIndex + 1)
        } else {
            surveyItem = intent.getSerializableExtra("SurveyItem") as SurveyItem
            initializeViews()
            setupRecycler()
        }
    }

    private fun initializeViews() {
        txtQuestion = binding.txtItemText
        txtQuestion.text = surveyItem.surveyQuestion.substring(3)
        btn_back = binding.imgbtnBack
        btn_back.setOnClickListener()
        {
            doBack()
        }
        btn_action = binding.btnAction
        btn_action.setOnClickListener()
        {
            if (recyclerAdapter.selectedIndex == -1)
                Toast.makeText(this, "답변을 선택해주세요.", Toast.LENGTH_SHORT).show()
            else
                putSurveyAnswer()
        }
        recycler_answers = binding.recyclerAnswers
    }

    override fun doBack() {
        val aIntent: Intent
        aIntent = if (intent.getStringExtra("previousActivity").equals("Home"))
            Intent(this, HomeActivity::class.java)
        else if (intent.getStringExtra("previousActivity").equals("Weather"))
            Intent(this, WeatherDetailActivity::class.java)
        else
            Intent(this, ChatMainActivity::class.java)

        aIntent.putExtra("previousActivity", "Home")
        startActivity(aIntent)
        transferToBack()
        finish()
    }

    private fun getSurveyAnswer(surveyId: Int) {
        lifecycleScope.launch {
            viewModel.getSurveyAnswer(surveyId, resources.getStringArray(R.array.survey_questions))
        }
        viewModel.surveyItem.observe(this) {
            it?.let {
                surveyItem = it!!
                initializeViews()
                setupRecycler()
            }
        }
    }

    private fun setupRecycler() {
        recyclerAdapter = RecyclerSurveyAnswersAdapter(this, surveyItem.surveyAnswers)
        recycler_answers.adapter = recyclerAdapter
        recycler_answers.layoutManager = LinearLayoutManager(baseContext)
    }

    private fun putSurveyAnswer() {
        val selectedAnswer = recyclerAdapter.getSelectedAnswerItem()
        lifecycleScope.launch {
            viewModel.putSurveyAnswer(selectedAnswer, surveyItem.surveyId)
            {
                if (it.isSuccessful) {
                    val intent =
                        Intent(this@SelectSurveyAnswerActivity, AnswerAnimationActivity::class.java)
                    intent.putExtra("answer", selectedAnswer.answer)
                    startActivity(intent)
                    overFromUnder()
                    finish()
                } else {
                    Log.e("putSurveyAnswer", it.errorBody()!!.source().toString())
                    Toast.makeText(
                        this@SelectSurveyAnswerActivity,
                        "답변 선택에 실패했습니다.",
                        Toast.LENGTH_SHORT
                    ).show()

                    doBack()
                }
            }
        }
    }
}