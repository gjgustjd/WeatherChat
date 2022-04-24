package com.miso.misoweather.activity.updateRegion

import androidx.lifecycle.ViewModel
import com.miso.misoweather.model.DataStoreManager
import com.miso.misoweather.model.MisoRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

@HiltViewModel
class UpdateRegionViewModel @Inject constructor(private val repository: MisoRepository) :
    ViewModel() {

    val bigScaleRegion by lazy {
        repository.dataStoreManager.getPreference(DataStoreManager.BIGSCALE_REGION)
    }

    fun updateSurveyRegion(region: String) {
        repository.dataStoreManager.savePreference(DataStoreManager.SURVEY_REGION, region)
    }
}