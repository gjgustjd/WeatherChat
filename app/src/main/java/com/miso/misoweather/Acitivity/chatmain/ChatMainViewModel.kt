package com.miso.misoweather.Acitivity.chatmain

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.R
import com.miso.misoweather.Module.LiveDataModule.*
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.CommentRegisterRequestDto
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerData
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultData
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultResponseDto
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class ChatMainViewModel @Inject constructor(private val repository: MisoRepository) : ViewModel() {

    @MutableStringLiveData
    @Inject
    lateinit var surveyRegion: MutableLiveData<String>

    @MutableStringLiveData
    @Inject
    lateinit var bigScaleRegion: MutableLiveData<String>

    @MutableStringLiveData
    @Inject
    lateinit var misoToken: MutableLiveData<String>

    @MutableStringLiveData
    @Inject
    lateinit var defaultRegionId: MutableLiveData<String>

    @MutableNullableAnyLiveData
    @Inject
    lateinit var forecastBriefResponse: MutableLiveData<Any?>

    @MutableNullableAnyLiveData
    @Inject
    lateinit var dailyForecastResponse: MutableLiveData<Any?>

    @MutableNullableAnyLiveData
    @Inject
    lateinit var hourlyForecastResponse: MutableLiveData<Any?>

    @MutableNullableAnyLiveData
    @Inject
    lateinit var currentAirData: MutableLiveData<Any?>

    @MutableResponseLiveData
    @Inject
    lateinit var commentListResponse: MutableLiveData<Response<*>?>

    @MutableResponseLiveData
    @Inject
    lateinit var addCommentResponse: MutableLiveData<Response<*>?>

    lateinit var surveyQuestions: Array<String>
    var surveyAnswerMap: HashMap<Int, List<SurveyAnswerDto>> = HashMap()

    @Inject
    lateinit var surveyResultResponseDto: SurveyResultResponseDto

    @Inject
    lateinit var surveyMyAnswerResponseDto: SurveyMyAnswerResponseDto

    @MutableAnyArrayListLiveData
    @Inject
    lateinit var surveyItems: MutableLiveData<ArrayList<Any>>


    fun updateProperties() {
        repository.apply {
            surveyRegion.value = getPreference("surveyRegion")
            bigScaleRegion.value = getPreference("BigScaleRegion")
            misoToken.value = getPreference("misoToken")
            defaultRegionId.value = getPreference("defaultRegionId")
        }
    }

    fun getCommentList(commentId: Int?, size: Int) {
        repository.getCommentList(
            commentId,
            size,
            { call, response ->
                commentListResponse.value = response
            },
            { call, response ->
                commentListResponse.value = response
            },
            { call, throwable -> }
        )
    }

    fun removeSurveyRegion() {
        repository.apply {
            removePreference("surveyRegion")
            savePreferences()
        }
    }

    fun addComment(
        shortBigScale: String,
        commentRegisterRequestDto: CommentRegisterRequestDto,
    ) {
        repository.addComment(
            shortBigScale,
            commentRegisterRequestDto,
            { call, response ->
                addCommentResponse.value = response
            },
            { call, response ->
                addCommentResponse.value = response
            },
            { call, t ->
//                    Log.i("결과", "실패 : $t")
            },
        )
    }

    fun setupSurveyAnswerList(activity: ChatMainActivity) {
        surveyQuestions = activity.resources.getStringArray(R.array.survey_questions)
        surveyQuestions.forEachIndexed { i, item ->
            Log.i("surveyAnswer", "surveyId:" + (i + 1).toString())
            getSurveyAnswer(i + 1)
        }
    }

    fun getSurveyAnswer(surveyId: Int) {
        repository.getSurveyAnswers(
            surveyId,
            { call, response ->
                initializeDataAndSetupRecycler {
                    surveyAnswerMap[surveyId] = (response.body()!!).data.responseList
                }
            },
            { call, response -> },
            { call, t -> },
        )
    }

    fun getSurveyResult(shortBigScale: String) {
        Log.i("getSurveyResult", shortBigScale)
        repository.getSurveyResults(
            shortBigScale,
            { call, reponse ->
                initializeDataAndSetupRecycler {
                    surveyResultResponseDto = reponse.body()!!
                }
            },
            { call, reponse ->
            },
            { call, t ->

            },
        )
    }

    fun getSurveyMyAnswers(serverToken: String) {
        repository.getSurveyMyAnswers(
            serverToken,
            { call, reponse ->
                initializeDataAndSetupRecycler {
                    surveyMyAnswerResponseDto = reponse.body()!!
                }
            },
            { call, response ->
            },
            { call, throwable -> }
        )
    }

    private fun makeSurveyItems() {
        surveyItems.value = ArrayList()
        val comparator: Comparator<SurveyMyAnswerDto> = compareBy { it.surveyId }
        val sortedMyanswerList = surveyMyAnswerResponseDto.data.responseList.sortedWith(comparator)

        surveyQuestions.forEachIndexed { index, s ->
            val surveyItem = SurveyItem(
                index + 1,
                s,
                sortedMyanswerList[index],
                surveyAnswerMap[index + 1]!!,
                surveyResultResponseDto.data.responseList[index]
            )
            surveyItems.value = surveyItems.value!!.apply {
                add(surveyItem)
            }
        }
    }

    fun initializeDataAndSetupRecycler(func: () -> Unit) {
        func()
        if (isAllSurveyResponseInitialized())
            makeSurveyItems()
    }

    fun isAllSurveyResponseInitialized(): Boolean {
        return ((surveyAnswerMap.size >= surveyQuestions.size) &&
                (surveyMyAnswerResponseDto.data.responseList.size >= surveyQuestions.size) &&
                (surveyResultResponseDto.data.responseList.size >= surveyQuestions.size))
    }
}
