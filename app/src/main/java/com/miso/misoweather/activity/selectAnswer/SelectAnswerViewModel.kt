package com.miso.misoweather.activity.selectAnswer

import android.content.Context
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.activity.chatmain.SurveyItem
import com.miso.misoweather.R
import com.miso.misoweather.model.DTO.SurveyAddMyAnswer.SurveyAddMyAnswerRequestDto
import com.miso.misoweather.model.DTO.SurveyAddMyAnswer.SurveyAddMyAnswerResponseDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResult
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ActivityContext
import retrofit2.Response
import java.lang.Exception
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SelectAnswerViewModel @Inject constructor(private val repository: MisoRepository) :
    ViewModel() {
    @ActivityContext
    lateinit var context: Context

    val surveyItem: MutableLiveData<SurveyItem?> = MutableLiveData()
    val bigScaleRegion =
        repository.dataStoreManager.getPreference(DataStoreManager.BIGSCALE_REGION)
    val misoToken =
        repository.dataStoreManager.getPreference(DataStoreManager.MISO_TOKEN)

    private fun getBigShortScale(bigScale: String): String {
        try {
            val regionList = context.resources.getStringArray(R.array.regions_full)
            val index = regionList.indexOf(bigScale)
            val regionSmallList = context.resources.getStringArray(R.array.regions)

            return regionSmallList.get(index)
        } catch (e: Exception) {
            return ""
        }
    }

    suspend fun getSurveyAnswer(
        surveyId: Int,
        questions: Array<String>,
    ) {
        val response =
            repository.getSurveyAnswers(surveyId)
        if (response.isSuccessful) {
            surveyItem.value = SurveyItem(
                surveyId,
                questions[surveyId - 1],
                SurveyMyAnswerDto(false, "", -1),
                (response.body()!!).data.responseList,
                SurveyResult(listOf(), -1, listOf())
            )
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    suspend fun putSurveyAnswer(
        selectedAnswer: SurveyAnswerDto,
        surveyId: Int,
        action: (response: Response<SurveyAddMyAnswerResponseDto>) -> Unit
    ) {
        val shortBigScale = getBigShortScale(bigScaleRegion!!)
        val response =
            repository.putSurveyMyAnswer(
                misoToken!!,
                SurveyAddMyAnswerRequestDto(
                    selectedAnswer.answerId,
                    shortBigScale,
                    surveyId,
                )
            )
        if (response.isSuccessful) {
            repository.dataStoreManager.apply {
                savePreference(DataStoreManager.IS_SURVEYED, "true")
                savePreference(
                    DataStoreManager.LAST_SURVEYED_DATE,
                    ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString()
                )
                savePreference(
                    DataStoreManager.SURVEY_REGION,
                    shortBigScale
                )
            }
        }

        action(response)
    }
}