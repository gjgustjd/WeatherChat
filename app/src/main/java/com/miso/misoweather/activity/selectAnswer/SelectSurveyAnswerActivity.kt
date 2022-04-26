package com.miso.misoweather.activity.selectAnswer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.miso.misoweather.activity.answerAnimationActivity.AnswerAnimationActivity
import com.miso.misoweather.activity.chatmain.ChatMainActivity
import com.miso.misoweather.activity.chatmain.SurveyItem
import com.miso.misoweather.activity.home.HomeActivity
import com.miso.misoweather.activity.weatherdetail.WeatherDetailActivity
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySurveyAnswerBinding
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class SelectSurveyAnswerActivity : MisoActivity() {
    private val viewModel: SelectAnswerViewModel by viewModels()
    private lateinit var binding: ActivitySurveyAnswerBinding
    val surveyItem = MutableLiveData<SurveyItem?>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_survey_answer)
        binding.lifecycleOwner = this
        binding.activity = this
        binding.viewModel = viewModel
        lifecycleScope.launch {
            surveyItem.value =
                (intent.getSerializableExtra("SurveyItem") as SurveyItem?) ?: getSurveyItem()
        }
    }

    private suspend fun getSurveyItem() = lifecycleScope.async {
        val questions = resources.getStringArray(R.array.survey_questions)
        val randomIndex = Random.nextInt(questions.size)
        viewModel.getSurveyAnswer(
            randomIndex + 1,
            resources.getStringArray(R.array.survey_questions)
        )
    }.await()


    fun submitAnswer() {
        val adapter = binding.recyclerAnswers.adapter as RecyclerSurveyAnswersAdapter
        if (adapter.selectedIndex == -1)
            Toast.makeText(this, "답변을 선택해주세요.", Toast.LENGTH_SHORT).show()
        else
            putSurveyAnswer()
    }

    override fun doBack() {
        val aIntent: Intent = if (intent.getStringExtra("previousActivity").equals("Home"))
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

    private fun putSurveyAnswer() {
        val adapter = binding.recyclerAnswers.adapter as RecyclerSurveyAnswersAdapter
        val selectedAnswer = adapter.getSelectedAnswerItem()
        lifecycleScope.launch {
            viewModel.putSurveyAnswer(
                selectedAnswer,
                surveyItem.value!!.surveyId
            )
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