package com.miso.misoweather.Acitivity.selectAnswer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.answerAnimationActivity.AnswerAnimationActivity
import com.miso.misoweather.Acitivity.chatmain.ChatMainActivity
import com.miso.misoweather.Acitivity.chatmain.SurveyItem
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySurveyAnswerBinding
import com.miso.misoweather.model.DTO.SurveyAddMyAnswer.SurveyAddMyAnswerRequestDto
import com.miso.misoweather.model.DTO.SurveyAddMyAnswer.SurveyAddMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResult
import com.miso.misoweather.model.TransportManager
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.random.Random

@RequiresApi(Build.VERSION_CODES.O)
class SelectSurveyAnswerActivity : MisoActivity() {
    lateinit var binding: ActivitySurveyAnswerBinding
    lateinit var btn_back: ImageButton
    lateinit var btn_action: Button
    lateinit var txtQuestion: TextView
    lateinit var surveyItem: SurveyItem
    lateinit var recycler_answers: RecyclerView
    lateinit var recyclerAdapter: RecyclerSurveyAnswersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySurveyAnswerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkAndInitializeViews()
    }

    fun checkAndInitializeViews() {
        if (intent.getSerializableExtra("SurveyItem") == null) {
            var questions = resources.getStringArray(R.array.survey_questions)
            var randomIndex = Random.nextInt(questions.size)
            getSurveyAnswer(randomIndex + 1)
        } else {
            surveyItem = intent.getSerializableExtra("SurveyItem") as SurveyItem
            initializeViews()
            setupRecycler()
        }
    }

    fun initializeViews() {
        txtQuestion = binding.txtItemText
        txtQuestion.text = surveyItem.surveyQuestion.substring(3)
        btn_back = binding.imgbtnBack
        btn_back.setOnClickListener()
        {
            var intent = Intent(this, ChatMainActivity::class.java)
            intent.putExtra("previousActivity", "Home")
            startActivity(intent)
            transferToBack()
            finish()
        }
        btn_action = binding.btnAction
        btn_action.setOnClickListener()
        {
            putSurveyAnswer()
        }
        recycler_answers = binding.recyclerAnswers
    }

    fun getSurveyAnswer(surveyId: Int) {
        val callGetSurveyAnswer =
            TransportManager.getRetrofitApiObject<SurveyAnswerResponseDto>()
                .getSurveyAnswers(surveyId)

        TransportManager.requestApi(callGetSurveyAnswer, { call, reponse ->
            var questions = resources.getStringArray(R.array.survey_questions)
            surveyItem = SurveyItem(
                surveyId,
                questions[surveyId - 1],
                SurveyMyAnswerDto(false, "", -1),
                (reponse.body()!!).data.responseList,
                SurveyResult(listOf(), -1, listOf())
            )
            initializeViews()
            setupRecycler()
        }, { call, throwable ->

        })
    }

    fun setupRecycler() {
        recyclerAdapter = RecyclerSurveyAnswersAdapter(this, surveyItem.surveyAnswers)
        recycler_answers.adapter = recyclerAdapter
        recycler_answers.layoutManager = LinearLayoutManager(baseContext)
    }

    fun putSurveyAnswer() {
        var selectedAnswer = recyclerAdapter.getSelectedAnswerItem()
        var callPutMyAnser =
            TransportManager.getRetrofitApiObject<SurveyAddMyAnswerResponseDto>().putSurveyMyAnser(
                getPreference("misoToken")!!,
                SurveyAddMyAnswerRequestDto(
                    selectedAnswer.answerId,
                    getBigShortScale(getPreference("bigScale")!!),
                    surveyItem.surveyId
                )
            )
        TransportManager.requestApi(
            callPutMyAnser,
            { call, response ->
                addPreferencePair("isSurveyed","true")
                addPreferencePair("LastSurveyedDate",
                    LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString())
                savePreferences()
                var intent = Intent(this, AnswerAnimationActivity::class.java)
                intent.putExtra("answer",selectedAnswer.answer)
                startActivity(intent)
                overFromUnder()
                finish()
            },
            { call, throwable ->

            })
    }
}