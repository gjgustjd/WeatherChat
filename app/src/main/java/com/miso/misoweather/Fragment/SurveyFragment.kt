package com.miso.misoweather.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.chatmain.RecyclerSurveysAdapter
import com.miso.misoweather.Acitivity.chatmain.SurveyItem
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.FragmentSurveyBinding
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyQuestion
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResult
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultResponseDto
import com.miso.misoweather.model.TransportManager

class SurveyFragment : Fragment() {
    lateinit var binding: FragmentSurveyBinding
    lateinit var recyclerSurvey: RecyclerView
    lateinit var recyclerSurveysAdapter: RecyclerSurveysAdapter
    lateinit var surveyQuestions: Array<String>
    lateinit var surveyAnswerList: ArrayList<List<SurveyAnswerDto>>
    lateinit var surveyResultResponseDto: SurveyResultResponseDto
    lateinit var surveyMyAnswerResponseDto: SurveyMyAnswerResponseDto
    lateinit var surveyItems: ArrayList<SurveyItem>
    lateinit var activity: MisoActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSurveyBinding.inflate(layoutInflater)
        activity = getActivity() as MisoActivity
        recyclerSurvey = binding.recyclerSurveys
        surveyAnswerList = ArrayList()
        setupSurveyAnswerList()
        setupSurveyResult(activity.getBigShortScale(activity.getPreference("bigScale")!!))
        setupSurveyMyAnswer()
    }

    fun setupRecyclerSurveys() {
        recyclerSurveysAdapter = RecyclerSurveysAdapter(requireActivity(),surveyItems)
        recyclerSurvey.adapter = recyclerSurveysAdapter
        recyclerSurvey.layoutManager = LinearLayoutManager(requireActivity().baseContext)
    }

    fun setupSurveyAnswerList() {
        surveyQuestions = activity.resources.getStringArray(R.array.survey_questions)

        surveyQuestions.forEachIndexed() { i, item ->
            getSurveyAnswer(i + 1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = binding.root
        // 처리
        return view
    }

    fun getSurveyAnswer(surveyId: Int) {
        val callGetSurveyAnswer =
            TransportManager.getRetrofitApiObject<SurveyAnswerResponseDto>()
                .getSurveyAnswers(surveyId)

        TransportManager.requestApi(callGetSurveyAnswer, { call, reponse ->
            initializeDataAndSetupRecycler {
                surveyAnswerList.add((reponse.body()!!).data.responseList)
            }
        }, { call, throwable ->

        })
    }

    fun setupSurveyResult(bigShortScale: String) {
        val callGetSurveyResult =
            TransportManager.getRetrofitApiObject<SurveyResultResponseDto>()
                .getSurveyResults(bigShortScale)

        TransportManager.requestApi(callGetSurveyResult, { call, reponse ->

            initializeDataAndSetupRecycler {
                surveyResultResponseDto = reponse.body()!!
            }
        }, { call, throwable ->

        })
    }

    fun setupSurveyMyAnswer() {
        val callGetSurveyMyAnswer =
            TransportManager.getRetrofitApiObject<SurveyMyAnswerResponseDto>()
                .getSurveyMyAnswers((requireActivity() as MisoActivity).getPreference("misoToken")!!)

        TransportManager.requestApi(callGetSurveyMyAnswer, { call, reponse ->
            initializeDataAndSetupRecycler {
                surveyMyAnswerResponseDto = reponse.body()!!
            }
        }, { call, throwable ->

        })
    }

    fun drawSurveyRecycler() {
        makeSurveyItems()
        setupRecyclerSurveys()
    }

    fun makeSurveyItems() {
        surveyItems = ArrayList()

        surveyQuestions.forEachIndexed { index, s ->
            surveyItems.add(
                SurveyItem(
                    index + 1,
                    s,
                    surveyMyAnswerResponseDto.data.responseList[index],
                    surveyAnswerList[index],
                    surveyResultResponseDto.data.responseList[index]
                )
            )
        }
    }

    fun initializeDataAndSetupRecycler(func: () -> Unit) {
        func()
        if (isAllSurveyResponseInitialized())
            drawSurveyRecycler()
    }

    fun isAllSurveyResponseInitialized(): Boolean {
        return ((surveyAnswerList.size >= surveyQuestions.size) &&
                this::surveyMyAnswerResponseDto.isInitialized &&
                this::surveyResultResponseDto.isInitialized)
    }
}