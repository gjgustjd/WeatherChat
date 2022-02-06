package com.miso.misoweather.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.chatmain.RecyclerSurveysAdapter
import com.miso.misoweather.R
import com.miso.misoweather.databinding.FragmentSurveyBinding
import com.miso.misoweather.model.DTO.SurveyQuestion

class SurveyFragment : Fragment() {
    lateinit var binding: FragmentSurveyBinding
    lateinit var recyclerSurvey: RecyclerView
    lateinit var recyclerSurveysAdapter: RecyclerSurveysAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSurveyBinding.inflate(layoutInflater)
        recyclerSurvey = binding.recyclerSurveys
        setupRecyclerSurveys()
    }

    fun setupRecyclerSurveys() {
        recyclerSurveysAdapter = RecyclerSurveysAdapter(requireActivity(), getSurveyList())
        recyclerSurvey.adapter = recyclerSurveysAdapter
        recyclerSurvey.layoutManager = LinearLayoutManager(requireActivity().baseContext)
    }

    fun getSurveyList(): ArrayList<SurveyQuestion> {
        var questions = requireActivity().resources.getStringArray(R.array.survey_questions)
        var surveyQuestions = ArrayList<SurveyQuestion>()

        for (i in 0..questions.size - 1) {
            val surveyQuestion = SurveyQuestion(questions[i], i + 1)
            surveyQuestions.add(surveyQuestion)
        }

        return surveyQuestions
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = binding.root
        // 처리
        return view
    }
}