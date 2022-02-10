package com.miso.misoweather.Acitivity.chatmain

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.databinding.ListItemSurveyBinding
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto

class RecyclerSurveysAdapter(var context: Context, var surveyItems: List<SurveyItem>) :
    RecyclerView.Adapter<RecyclerSurveysAdapter.Holder>() {

    var viewHolders: ArrayList<Holder> = ArrayList()

    override fun getItemCount(): Int {
        return surveyItems.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.txtTitle.text = surveyItems.get(position).surveyQuestion
        viewHolders.add(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.list_item_survey, parent, false)
        return Holder(ListItemSurveyBinding.bind(view))
    }

    class Holder(itemView: ListItemSurveyBinding) : RecyclerView.ViewHolder(itemView.root) {
        var surveyAnswers = ArrayList<SurveyAnswerDto>()
        var surveyId:Int = -1
        var txtTitle = itemView.txtTitle
    }

}