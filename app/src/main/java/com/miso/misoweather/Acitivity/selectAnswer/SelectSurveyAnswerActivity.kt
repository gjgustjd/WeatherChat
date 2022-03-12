package com.miso.misoweather.Acitivity.selectAnswer

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.answerAnimationActivity.AnswerAnimationActivity
import com.miso.misoweather.Acitivity.chatmain.ChatMainActivity
import com.miso.misoweather.Acitivity.chatmain.SurveyItem
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivitySurveyAnswerBinding
import com.miso.misoweather.model.DTO.SurveyAddMyAnswer.SurveyAddMyAnswerRequestDto
import com.miso.misoweather.model.DTO.SurveyAddMyAnswer.SurveyAddMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResult
import com.miso.misoweather.model.MisoRepository
import com.miso.misoweather.model.TransportManager
import kotlinx.coroutines.selects.select
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
    lateinit var viewModel: SelectAnswerViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivitySurveyAnswerBinding.inflate(layoutInflater)
        viewModel = SelectAnswerViewModel(MisoRepository.getInstance(applicationContext))
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
        var aIntent: Intent
        if (intent.getStringExtra("previousActivity").equals("Home"))
            aIntent = Intent(this, HomeActivity::class.java)
        else
            aIntent = Intent(this, ChatMainActivity::class.java)

        aIntent.putExtra("previousActivity", "Home")
        startActivity(aIntent)
        transferToBack()
        finish()
    }

    fun getSurveyAnswer(surveyId: Int) {
        viewModel.getSurveyAnswer(surveyId, resources.getStringArray(R.array.survey_questions))
        viewModel.surveyItem.observe(this, {
            surveyItem = it!!
            initializeViews()
            setupRecycler()
        })
    }

    fun setupRecycler() {
        recyclerAdapter = RecyclerSurveyAnswersAdapter(this, surveyItem.surveyAnswers)
        recycler_answers.adapter = recyclerAdapter
        recycler_answers.layoutManager = LinearLayoutManager(baseContext)
    }

    fun putSurveyAnswer() {
        var selectedAnswer = recyclerAdapter.getSelectedAnswerItem()
        viewModel.putSurveyAnswer(selectedAnswer, surveyItem.surveyId)
        viewModel.surveyAnswerResponse.observe(this, {
            if (it == null) {
            } else {
                if (it.isSuccessful) {
                    var intent = Intent(this, AnswerAnimationActivity::class.java)
                    intent.putExtra("answer", selectedAnswer.answer)
                    startActivity(intent)
                    overFromUnder()
                    finish()
                } else {
                }
            }
        })
    }
}