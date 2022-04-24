package com.miso.misoweather.activity.selectAnswer

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.databinding.ListSurveyAnswerBinding
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import java.lang.Exception

class RecyclerSurveyAnswersAdapter(
    private val context: Context,
    private val surveyItems: List<SurveyAnswerDto>
) :
    RecyclerView.Adapter<RecyclerSurveyAnswersAdapter.Holder>() {

    private var viewHolders: ArrayList<Holder> = ArrayList()
    var selectedIndex: Int = -1

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return surveyItems.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        try {
            holder.apply {
                txtAnswer.text = surveyItems[position].answer
                txtDescription.text = surveyItems[position].answerDescription
                itemView.setOnClickListener()
                {
                    if (selectedIndex != -1)
                        applyHolderSelection(selectedIndex, false)
                    selectedIndex = position
                    applyHolderSelection(selectedIndex, true)
                }
            }

            viewHolders.add(holder)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun getSelectedAnswerItem(): SurveyAnswerDto {
        return surveyItems.get(selectedIndex)
    }

    private fun applyHolderSelection(position: Int, value: Boolean) {
        if (value)
            selectHolder(position)
        else
            unselectHolder(position)
    }

    private fun selectHolder(position: Int) {
        viewHolders[position].apply {
            itemView.background =
                context.resources.getDrawable(R.drawable.toggle_track_background_purple, null)
            txtAnswer.setTextColor(Color.WHITE)
            txtDescription.setTextColor(Color.WHITE)
            imgCheck.visibility = View.VISIBLE
        }
    }

    private fun unselectHolder(position: Int) {
        viewHolders[position].apply {
            itemView.background =
                context.resources.getDrawable(R.drawable.toggle_track_background_stroke, null)
            txtAnswer.setTextColor(Color.BLACK)
            txtDescription.setTextColor(Color.BLACK)
            imgCheck.visibility = View.GONE
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_survey_answer, parent, false)
        return Holder(ListSurveyAnswerBinding.bind(view))
    }

    class Holder(itemView: ListSurveyAnswerBinding) : RecyclerView.ViewHolder(itemView.root) {
        val txtAnswer = itemView.txtAnswer
        val txtDescription = itemView.txtDescription
        val imgCheck = itemView.imgCheck
        val itemView = itemView.itemView
    }

}