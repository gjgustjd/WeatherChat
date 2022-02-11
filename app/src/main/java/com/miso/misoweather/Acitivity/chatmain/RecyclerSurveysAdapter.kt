package com.miso.misoweather.Acitivity.chatmain

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.selectAnswer.SelectSurveyAnswerActivity
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ListItemSurveyBinding
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import java.io.Serializable
import java.lang.Exception

class RecyclerSurveysAdapter(var context: Context, var surveyItems: List<SurveyItem>) :
    RecyclerView.Adapter<RecyclerSurveysAdapter.Holder>() {

    var viewHolders: ArrayList<Holder> = ArrayList()


    override fun getItemCount(): Int {
        return surveyItems.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        try {
            var surveyItem = surveyItems.get(position)
            var myAnswer = surveyItem.surveyMyAnswer
            var answerList = surveyItem.surveyAnswers
            var surveyResult = surveyItem.surveyResult

            holder.txtTitle.text = surveyItems.get(position).surveyQuestion
            holder.txtMyAnswer.text = myAnswer.memberAnswer
            holder.txtFirstScore.text = surveyResult.keyList.get(0)?.toString()
            holder.txtSecondScore.text = surveyResult.keyList.get(1)?.toString()
            holder.txtThirdScore.text = surveyResult.keyList.get(2)?.toString()

            var firstRatio = surveyResult.valueList.get(0).toString()
            var secondRatio = surveyResult.valueList.get(1).toString()
            var thirdRatio = surveyResult.valueList.get(2).toString()
            holder.txtFirstRatio.text = firstRatio+"%"
            holder.txtSecondRatio.text = secondRatio+"%"
            holder.txtThirdRatio.text = thirdRatio+"%"
            holder.progress_first.progress = firstRatio.toInt()
            holder.progress_second.progress = secondRatio.toInt()
            holder.progress_third.progress = thirdRatio.toInt()

            if(!myAnswer.answered || myAnswer.memberAnswer==null)
            {
                holder.imgIsAnswered.setImageDrawable(context.resources.getDrawable(R.drawable.icon_unanswered))
                holder.txtMyAnswer.setTextColor(context.resources.getColor(R.color.textBlack))
                holder.txtMyAnswer.text = "답변하기"
            }

            holder.myAnswerLayout.setOnClickListener{
               var misoActivity = context as MisoActivity
                var intent = Intent(context,SelectSurveyAnswerActivity::class.java)
                intent.putExtra("SurveyItem",surveyItem)
                misoActivity.startActivity(intent)
                misoActivity.transferToNext()
                misoActivity.finish()
            }

            viewHolders.add(holder)
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_survey, parent, false)
        return Holder(ListItemSurveyBinding.bind(view))
    }

    class Holder(itemView: ListItemSurveyBinding) : RecyclerView.ViewHolder(itemView.root) {
        var surveyAnswers = ArrayList<SurveyAnswerDto>()
        var surveyId: Int = -1
        var txtTitle = itemView.txtTitle
        var txtMyAnswer = itemView.txtMyScore
        var txtFirstScore = itemView.txtFirstScore
        var txtSecondScore = itemView.txtSecondScore
        var txtThirdScore = itemView.txtThirdScore
        var txtFirstRatio = itemView.txtFirstRatio
        var txtSecondRatio = itemView.txtSecondRatio
        var txtThirdRatio = itemView.txtThirdRatio
        var progress_first = itemView.progressFirst
        var progress_second = itemView.progressSecond
        var progress_third = itemView.progressThird
        var imgIsAnswered = itemView.imgIsanswered
        var myAnswerLayout = itemView.myAnswerLayout



    }

}