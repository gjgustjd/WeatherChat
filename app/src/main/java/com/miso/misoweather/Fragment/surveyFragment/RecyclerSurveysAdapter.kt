package com.miso.misoweather.Fragment.surveyFragment

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.chatmain.SurveyItem
import com.miso.misoweather.Acitivity.selectAnswer.SelectSurveyAnswerActivity
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ListItemSurveyBinding
import java.lang.Exception

class RecyclerSurveysAdapter(var context: Context, var surveyItems: List<SurveyItem>) :
    RecyclerView.Adapter<RecyclerSurveysAdapter.Holder>() {

    var viewHolders: ArrayList<Holder> = ArrayList()

    override fun getItemCount(): Int {
        return surveyItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
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
            holder.txtFirstRatio.text = firstRatio + "%"
            holder.txtSecondRatio.text = secondRatio + "%"
            holder.txtThirdRatio.text = thirdRatio + "%"
            holder.progress_first.startProgressAnimation(firstRatio.toInt())
            holder.progress_second.startProgressAnimation(secondRatio.toInt(),250)
            holder.progress_third.startProgressAnimation(thirdRatio.toInt(),500)

            if (!myAnswer.answered || myAnswer.memberAnswer == null) {
                holder.imgIsAnswered.setImageDrawable(context.resources.getDrawable(R.drawable.icon_unanswered))
                holder.txtMyAnswer.setTextColor(context.resources.getColor(R.color.textBlack))
                holder.txtMyAnswer.text = "답변하기"
                holder.imgIconNext.visibility = View.VISIBLE
            }

            holder.myAnswerLayout.setOnClickListener {
                if (!surveyItems[position].surveyMyAnswer.answered) {
                    var misoActivity = context as MisoActivity
                    var intent = Intent(context, SelectSurveyAnswerActivity::class.java)
                    intent.putExtra("SurveyItem", surveyItem)
                    misoActivity.startActivity(intent)
                    misoActivity.transferToNext()
                    misoActivity.finish()
                }
            }

            if (surveyResult.valueList.filter { !it.equals(0) }.size == 0) {
                holder.txtEmptySurvey.visibility = View.VISIBLE
                holder.txtOtherTitle.visibility = View.GONE
                holder.progressLayout.visibility = View.GONE
            }

            viewHolders.add(holder)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_survey, parent, false)
        return Holder(ListItemSurveyBinding.bind(view))
    }

    fun ProgressBar.startProgressAnimation(toValue: Int, delay: Int?=0) {
        progress=0
        var animator = ObjectAnimator.ofInt(this, "progress", 0, toValue)
        animator.duration = 1000
        animator.startDelay = delay!!.toLong()
        animator.interpolator = DecelerateInterpolator()
        animator.start()
    }

    class Holder(itemView: ListItemSurveyBinding) : RecyclerView.ViewHolder(itemView.root) {
        var surveyId: Int = -1
        var txtTitle = itemView.txtTitle
        var txtMyAnswer = itemView.txtMyScore
        var txtFirstScore = itemView.txtFirstScore
        var txtSecondScore = itemView.txtSecondScore
        var txtThirdScore = itemView.txtThirdScore
        var txtFirstRatio = itemView.txtFirstRatio
        var txtSecondRatio = itemView.txtSecondRatio
        var txtThirdRatio = itemView.txtThirdRatio
        var progressLayout = itemView.progressLayout
        var progress_first = itemView.progressFirst
        var progress_second = itemView.progressSecond
        var progress_third = itemView.progressThird
        var imgIsAnswered = itemView.imgIsanswered
        var myAnswerLayout = itemView.myAnswerLayout
        var imgIconNext = itemView.imgIconNext
        var txtEmptySurvey = itemView.txtEmptySurvey
        var txtOtherTitle = itemView.txtOthersTitle

    }

}