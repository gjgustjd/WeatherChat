package com.miso.misoweather.Fragment.surveyFragment

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.chatmain.ChatMainActivity
import com.miso.misoweather.Acitivity.chatmain.SurveyItem
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.FragmentSurveyBinding
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultResponseDto
import com.miso.misoweather.model.TransportManager

@RequiresApi(Build.VERSION_CODES.O)
class SurveyFragment : Fragment() {
    lateinit var binding: FragmentSurveyBinding
    lateinit var recyclerSurvey: RecyclerView
    lateinit var recyclerSurveysAdapter: RecyclerSurveysAdapter
    lateinit var surveyQuestions: Array<String>
    lateinit var surveyAnswerMap: HashMap<Int, List<SurveyAnswerDto>>
    lateinit var surveyResultResponseDto: SurveyResultResponseDto
    lateinit var surveyMyAnswerResponseDto: SurveyMyAnswerResponseDto
    lateinit var surveyItems: ArrayList<SurveyItem>
    lateinit var currentLocation: String
    lateinit var bigShortScale: String
    lateinit var activity: ChatMainActivity
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSurveyBinding.inflate(layoutInflater)
        activity = getActivity() as ChatMainActivity
        bigShortScale = activity.selectedRegion
        initializeView()
        setupSurveyAnswerList()
        setupSurveyResult(bigShortScale)
        setupSurveyMyAnswer()
    }

    fun initializeView() {
        currentLocation = activity.selectedRegion
        recyclerSurvey = binding.recyclerSurveys
        surveyAnswerMap = HashMap()
    }


    fun setupRecyclerSurveys() {
        try {
            recyclerSurveysAdapter = RecyclerSurveysAdapter(activity, surveyItems)
            recyclerSurvey.adapter = recyclerSurveysAdapter
            recyclerSurvey.layoutManager = LinearLayoutManager(activity.baseContext)
        }catch (e:Exception)
        {
            e.printStackTrace()
        }
    }

    fun setupSurveyAnswerList() {
        surveyQuestions = activity.resources.getStringArray(R.array.survey_questions)

        surveyQuestions.forEachIndexed { i, item ->
            Log.i("surveyAnswer", "surveyId:" + (i + 1).toString())
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
                surveyAnswerMap.put(surveyId,(reponse.body()!!).data.responseList)
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
        val comparator: Comparator<SurveyMyAnswerDto> = compareBy { it.surveyId }
        var sortedMyanswerList = surveyMyAnswerResponseDto.data.responseList.sortedWith(comparator)

        surveyQuestions.forEachIndexed { index, s ->
            val surveyItem = SurveyItem(
                index + 1,
                s,
                sortedMyanswerList[index],
                surveyAnswerMap.get(index+1)!!,
                surveyResultResponseDto.data.responseList[index]
            )
            Log.i("surveyItem", surveyItem.surveyId.toString())
            Log.i("surveyItem", surveyItem.surveyQuestion)
            Log.i("surveyItem", surveyItem.surveyAnswers.get(0).answer)
            surveyItems.add(
                surveyItem
            )
        }
    }

    fun initializeDataAndSetupRecycler(func: () -> Unit) {
        func()
        if (isAllSurveyResponseInitialized())
            drawSurveyRecycler()
    }

    fun isAllSurveyResponseInitialized(): Boolean {
        return ((surveyAnswerMap.size >= surveyQuestions.size) &&
                this::surveyMyAnswerResponseDto.isInitialized &&
                this::surveyResultResponseDto.isInitialized)
    }
}