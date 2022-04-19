package com.miso.misoweather.Acitivity.updateRegion

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.miso.misoweather.Module.LiveDataModule.*
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.scopes.ActivityRetainedScoped
import javax.inject.Inject

@HiltViewModel
class UpdateRegionViewModel @Inject constructor(private val repository: MisoRepository) : ViewModel() {

    @MutableNullableStringLiveData
    @Inject
    lateinit var surveyRegion: MutableLiveData<String?>

    @MutableNullableStringLiveData
    @Inject
    lateinit var bigScaleRegion: MutableLiveData<String?>

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

    fun updateSurveyRegion(region: String) {
        repository.addPreferencePair("surveyRegion", region)
        repository.savePreferences()
    }
}