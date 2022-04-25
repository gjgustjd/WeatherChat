package com.miso.misoweather.activity.home

import android.view.View
import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultResponseDto

object HomeBindingAdapter {
    @BindingAdapter("emptyLayoutVisibility")
    @JvmStatic
    fun setupVisibility(view: View, surveyResultResponseDto: SurveyResultResponseDto?) {
        surveyResultResponseDto?.let { it ->
            it.data.responseList.first { it.surveyId == 2 }.let {
                if (it.keyList.all { it.toString().isNullOrBlank() } &&
                    it.valueList.all { it.toString().isNullOrBlank() })
                    view.visibility = View.VISIBLE
                else
                    view.visibility = View.GONE
            }
        }
    }

    @BindingAdapter("ItemVisibility")
    @JvmStatic
    fun setupItemVisibility(view: View, surveyResultResponseDto: SurveyResultResponseDto?) {
        surveyResultResponseDto?.let { it ->
            it.data.responseList.first { it.surveyId == 2 }.let {
                val index = view.tag.toString().toInt()
                if (it.keyList[index].toString().isNullOrBlank() &&
                    it.valueList[index].toString().isNullOrBlank()
                )
                    view.visibility = View.GONE
                else
                    view.visibility = View.VISIBLE
            }
        }
    }


    @BindingAdapter("answer")
    @JvmStatic
    fun setAnswer(view: TextView, surveyResultResponseDto: SurveyResultResponseDto?) {
        surveyResultResponseDto?.let { it ->
            it.data.responseList.first { it.surveyId == 2 }.let {
                val index = view.tag.toString().toInt()
                view.text = it.keyList[index].toString()
            }
        }
    }

    @BindingAdapter("ratio")
    @JvmStatic
    fun setRatio(view: TextView, surveyResultResponseDto: SurveyResultResponseDto?) {
        surveyResultResponseDto?.let { it ->
            it.data.responseList.first { it.surveyId == 2 }.let {
                val index = view.tag.toString().toInt()
                view.text = "${it.valueList[index]} %"
            }
        }
    }
}