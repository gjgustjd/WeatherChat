package com.miso.misoweather.Acitivity.chatmain

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.R
import com.miso.misoweather.model.DTO.CommentRegisterRequestDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultResponseDto
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class ChatMainViewModel @Inject constructor(private val repository: MisoRepository) : ViewModel() {
    val surveyItems by lazy { MutableLiveData<ArrayList<Any>>() }
    val commentListResponse by lazy { MutableLiveData<Response<*>?>() }
    val addCommentResponse by lazy { MutableLiveData<Response<*>?>() }

    val surveyRegion by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.SURVEY_REGION)
    }

    val bigScaleRegion by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.BIGSCALE_REGION)
    }
    val nickname by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.NICKNAME)
    }

    val misoToken by lazy { repository.dataStoreManager.getPreference(DataStoreManager.MISO_TOKEN) }


    lateinit var surveyQuestions: Array<String>
    var surveyAnswerMap: HashMap<Int, List<SurveyAnswerDto>> = HashMap()

    @Inject
    lateinit var surveyResultResponseDto: SurveyResultResponseDto

    @Inject
    lateinit var surveyMyAnswerResponseDto: SurveyMyAnswerResponseDto


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
        repository.dataStoreManager.removePreference(DataStoreManager.SURVEY_REGION)
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
