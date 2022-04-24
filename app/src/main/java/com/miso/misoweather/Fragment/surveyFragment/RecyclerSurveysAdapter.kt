package com.miso.misoweather.Fragment.surveyFragment

import android.animation.ObjectAnimator
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View.*
import android.view.ViewGroup
import android.view.animation.DecelerateInterpolator
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.activity.chatmain.SurveyItem
import com.miso.misoweather.activity.selectAnswer.SelectSurveyAnswerActivity
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ListItemSurveyBinding
import java.lang.Exception

class RecyclerSurveysAdapter(
    private val context: Context,
    private val surveyItems: List<SurveyItem>
) :
    RecyclerView.Adapter<RecyclerSurveysAdapter.Holder>() {

    private var viewHolders: ArrayList<Holder> = ArrayList()

    override fun getItemCount(): Int {
        return surveyItems.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: Holder, position: Int) {
        try {
            val surveyItem = surveyItems.get(position)
            val myAnswer = surveyItem.surveyMyAnswer
            val surveyResult = surveyItem.surveyResult

            holder.apply {
                txtTitle.text = surveyItems.get(position).surveyQuestion
                txtMyAnswer.text = myAnswer.memberAnswer
                txtFirstScore.text = surveyResult.keyList.get(0)?.toString()
                txtSecondScore.text = surveyResult.keyList.get(1)?.toString()
                txtThirdScore.text = surveyResult.keyList.get(2)?.toString()

                val firstRatio = surveyResult.valueList[0].toString()
                val secondRatio = surveyResult.valueList[1].toString()
                val thirdRatio = surveyResult.valueList[2].toString()
                txtFirstRatio.text = firstRatio + "%"
                txtSecondRatio.text = secondRatio + "%"
                txtThirdRatio.text = thirdRatio + "%"
                progress_first.startProgressAnimation(firstRatio.toInt())
                progress_second.startProgressAnimation(secondRatio.toInt(), 250)
                progress_third.startProgressAnimation(thirdRatio.toInt(), 500)

                if (!myAnswer.answered || myAnswer.memberAnswer == null) {
                    imgIsAnswered.setImageDrawable(
                        context.resources.getDrawable(
                            R.drawable.icon_unsurveyed_vector,
                            null
                        )
                    )
                    txtMyAnswer.setTextColor(context.resources.getColor(R.color.textBlack))
                    txtMyAnswer.text = "답변하기"
                    imgIconNext.visibility = VISIBLE
                }

                myAnswerLayout.setOnClickListener {
                    if (!surveyItems[position].surveyMyAnswer.answered) {
                        val misoActivity = context as MisoActivity
                        val intent = Intent(context, SelectSurveyAnswerActivity::class.java)
                        intent.putExtra("SurveyItem", surveyItem)
                        misoActivity.startActivity(intent)
                        misoActivity.transferToNext()
                        misoActivity.finish()
                    }
                }

                if (surveyResult.valueList.none { it != 0 }) {
                    txtEmptySurvey.visibility = VISIBLE
                    txtOtherTitle.visibility = GONE
                    progressLayout.visibility = GONE
                }

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

    private fun ProgressBar.startProgressAnimation(toValue: Int, delay: Int? = 0) {
        progress = 0
        val animator = ObjectAnimator.ofInt(this, "progress", 0, toValue)
        animator.apply {
            duration = 1000
            startDelay = delay!!.toLong()
            interpolator = DecelerateInterpolator()
            start()
        }
    }

    class Holder(itemView: ListItemSurveyBinding) : RecyclerView.ViewHolder(itemView.root) {
        var surveyId: Int = -1
        val txtTitle = itemView.txtTitle
        val txtMyAnswer = itemView.txtMyScore
        val txtFirstScore = itemView.txtFirstScore
        val txtSecondScore = itemView.txtSecondScore
        val txtThirdScore = itemView.txtThirdScore
        val txtFirstRatio = itemView.txtFirstRatio
        val txtSecondRatio = itemView.txtSecondRatio
        val txtThirdRatio = itemView.txtThirdRatio
        val progressLayout = itemView.progressLayout
        val progress_first = itemView.progressFirst
        val progress_second = itemView.progressSecond
        val progress_third = itemView.progressThird
        val imgIsAnswered = itemView.imgIsanswered
        val myAnswerLayout = itemView.myAnswerLayout
        val imgIconNext = itemView.imgIconNext
        val txtEmptySurvey = itemView.txtEmptySurvey
        val txtOtherTitle = itemView.txtOthersTitle

    }

}