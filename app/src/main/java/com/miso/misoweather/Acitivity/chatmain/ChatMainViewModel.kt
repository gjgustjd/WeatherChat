package com.miso.misoweather.Acitivity.chatmain

import android.content.Context
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.miso.misoweather.R
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.DTO.CommentRegisterRequestDto
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResultResponseDto
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository2
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.async
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class ChatMainViewModel @Inject constructor(private val repository: MisoRepository2) : ViewModel() {
    val surveyItems by lazy { MutableLiveData<ArrayList<SurveyItem>>() }
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

    private lateinit var surveyQuestions: Array<String>

    suspend fun getCommentList(
        commentId: Int?,
        size: Int,
        action: (response: Response<CommentListResponseDto>) -> Unit
    ) = action(repository.getCommentList(commentId, size))

    fun removeSurveyRegion() {
        repository.dataStoreManager.removePreference(DataStoreManager.SURVEY_REGION)
    }

    suspend fun addComment(
        shortBigScale: String,
        commentRegisterRequestDto: CommentRegisterRequestDto,
        action: (response: Response<GeneralResponseDto>) -> Unit
    ) = action(repository.addComment(shortBigScale, commentRegisterRequestDto))


    suspend fun setupSurveyAnswerList(context: Context) = viewModelScope.async {
        val surveyAnswerMap = HashMap<Int, List<SurveyAnswerDto>>()
        surveyQuestions = context.resources.getStringArray(R.array.survey_questions)
        surveyQuestions.forEachIndexed { i, _ ->
            surveyAnswerMap[i + 1] = getSurveyAnswer(i + 1).await()
        }

        surveyAnswerMap
    }

    private fun getSurveyAnswer(surveyId: Int) =
        viewModelScope.async { repository.getSurveyAnswers(surveyId).body()!!.data.responseList }

    private fun getSurveyResult(shortBigScale: String) =
        viewModelScope.async { repository.getSurveyResults(shortBigScale).body() }

    private fun getSurveyMyAnswers(serverToken: String) =
        viewModelScope.async { repository.getSurveyMyAnswers(serverToken).body() }

    private fun makeSurveyItems(
        surveyMyAnswerResponseDto: SurveyMyAnswerResponseDto,
        surveyAnswerMap: HashMap<Int, List<SurveyAnswerDto>>,
        surveyResultResponseDto: SurveyResultResponseDto
    ) {
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

    suspend fun setupSurveyItems(context: ChatMainActivity) {
        makeSurveyItems(
            getSurveyMyAnswers(misoToken).await()!!,
            setupSurveyAnswerList(context).await(),
            getSurveyResult(context.getBigShortScale(bigScaleRegion)).await()!!
        )
    }
}
