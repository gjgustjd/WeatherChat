package com.miso.misoweather.Acitivity.selectAnswer

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

class RecyclerSurveyAnswersAdapter(var context: Context, var surveyItems: List<SurveyAnswerDto>) :
    RecyclerView.Adapter<RecyclerSurveyAnswersAdapter.Holder>() {

    var viewHolders: ArrayList<Holder> = ArrayList()
    var selectedIndex: Int = -1

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemCount(): Int {
        return surveyItems.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        try {
            holder.txtAnswer.text = surveyItems.get(position).answer
            holder.txtDescription.text = surveyItems.get(position).answerDescription
            holder.itemView.setOnClickListener()
            {
                if (selectedIndex != -1)
                    applyHolderSelection(selectedIndex, false)
                selectedIndex = position
                applyHolderSelection(selectedIndex, true)
            }

            viewHolders.add(holder)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    fun getSelectedAnswerItem():SurveyAnswerDto
    {
        return surveyItems.get(selectedIndex)
    }

    fun applyHolderSelection(position: Int, value: Boolean) {
        if (value)
            selectHolder(position)
        else
            unselectHolder(position)
    }

    fun selectHolder(position: Int) {
        var selectedHolder = viewHolders.get(position)
        selectedHolder.itemView.background =
            context.resources.getDrawable(R.drawable.toggle_track_background_purple)
        selectedHolder.txtAnswer.setTextColor(Color.WHITE)
        selectedHolder.txtDescription.setTextColor(Color.WHITE)
        selectedHolder.imgCheck.visibility = View.VISIBLE
    }

    fun unselectHolder(position: Int) {
        var unselectedHolder = viewHolders.get(position)
        unselectedHolder.itemView.background =
            context.resources.getDrawable(R.drawable.toggle_track_background_stroke)
        unselectedHolder.txtAnswer.setTextColor(Color.BLACK)
        unselectedHolder.txtDescription.setTextColor(Color.BLACK)
        unselectedHolder.imgCheck.visibility = View.GONE
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_survey_answer, parent, false)
        return Holder(ListSurveyAnswerBinding.bind(view))
    }

    class Holder(itemView: ListSurveyAnswerBinding) : RecyclerView.ViewHolder(itemView.root) {
        var txtAnswer = itemView.txtAnswer
        var txtDescription = itemView.txtDescription
        var imgCheck = itemView.imgCheck
        var itemView = itemView.itemView
    }

}