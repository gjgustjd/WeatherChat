package com.miso.misoweather.activity.selectAnswer

import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.activity.chatmain.SurveyItem

object SelectAnswerBindingAdapter {
    @BindingAdapter("bindSelectSurveyAnswerData")
    @JvmStatic
    fun setSurveyAnswerData(view: RecyclerView, surveyItem: SurveyItem?) {
        surveyItem?.let {
            view.adapter = RecyclerSurveyAnswersAdapter(view.context, surveyItem.surveyAnswers)
        }
    }

    @BindingAdapter("surveyQuestionText")
    @JvmStatic
    fun setSurveyQuestionText(view: TextView, surveyItem: SurveyItem?) {
        surveyItem?.let {
            view.text = surveyItem.surveyQuestion.substring(3)
        }
    }
}