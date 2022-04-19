package com.miso.misoweather.Acitivity.selectAnswer

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.Acitivity.chatmain.SurveyItem
import com.miso.misoweather.Module.LiveDataModule.*
import com.miso.misoweather.R
import com.miso.misoweather.model.DTO.SurveyAddMyAnswer.SurveyAddMyAnswerRequestDto
import com.miso.misoweather.model.DTO.SurveyMyAnswer.SurveyMyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResponse.SurveyAnswerDto
import com.miso.misoweather.model.DTO.SurveyResultResponse.SurveyResult
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import retrofit2.Response
import java.lang.Exception
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@HiltViewModel
class SelectAnswerViewModel @Inject constructor(private val repository: MisoRepository) :
    ViewModel() {

    val surveyItem: MutableLiveData<SurveyItem?> = MutableLiveData()

    @MutableResponseLiveData
    @Inject
    lateinit var surveyAnswerResponse: MutableLiveData<Response<*>?>

    fun getSurveyAnswer(surveyId: Int, questions: Array<String>) {
        repository.getSurveyAnswers(
            surveyId,
            { call, response ->
                surveyItem.value = SurveyItem(
                    surveyId,
                    questions[surveyId - 1],
                    SurveyMyAnswerDto(false, "", -1),
                    (response.body()!!).data.responseList,
                    SurveyResult(listOf(), -1, listOf())
                )
            },
            { call, response -> },
            { call, t -> },
        )
    }

    fun getBigShortScale(bigScale: String): String {
        try {
            val regionList = repository.context.resources.getStringArray(R.array.regions_full)
            val index = regionList.indexOf(bigScale)
            val regionSmallList = repository.context.resources.getStringArray(R.array.regions)

            return regionSmallList.get(index)
        } catch (e: Exception) {
            return ""
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun putSurveyAnswer(selectedAnswer: SurveyAnswerDto, surveyId: Int) {
        Log.i("putSurveyAnswer", repository.getPreference("BigScaleRegion")!!)
        repository.putSurveyMyAnswer(
            repository.getPreference("misoToken")!!,
            SurveyAddMyAnswerRequestDto(
                selectedAnswer.answerId,
                getBigShortScale(repository.getPreference("BigScaleRegion")!!),
                surveyId
            ),
            { call, response ->
                repository.addPreferencePair("isSurveyed", "true")
                repository.addPreferencePair(
                    "LastSurveyedDate",
                    ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                        .format(DateTimeFormatter.ofPattern("yyyyMMdd")).toString()
                )
                repository.addPreferencePair(
                    "surveyRegion",
                    getBigShortScale(repository.getPreference("BigScaleRegion")!!)
                )
                repository.savePreferences()
                surveyAnswerResponse.value = response!!
            },
            { call, response -> },
            { call, throwable -> }
        )
    }
}