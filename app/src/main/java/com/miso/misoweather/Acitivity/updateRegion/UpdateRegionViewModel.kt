package com.miso.misoweather.Acitivity.updateRegion

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.model.MisoRepository

class UpdateRegionViewModel(private val repository: MisoRepository) : ViewModel() {
    val surveyRegion: MutableLiveData<String?> = MutableLiveData()
    val bigScaleRegion: MutableLiveData<String?> = MutableLiveData()

    fun updateProperties() {
        setupBigScaleRegion()
        setupSurveyRegion()
    }

    fun setupSurveyRegion() {
        surveyRegion.value = repository.getPreference("surveyRegion")
    }

    fun setupBigScaleRegion() {
        bigScaleRegion.value = repository.getPreference("BigScaleRegion")
    }
}