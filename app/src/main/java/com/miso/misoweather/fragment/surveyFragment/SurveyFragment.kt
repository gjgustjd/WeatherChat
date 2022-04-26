package com.miso.misoweather.fragment.surveyFragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.miso.misoweather.R
import com.miso.misoweather.activity.chatmain.ChatMainActivity
import com.miso.misoweather.activity.chatmain.ChatMainViewModel
import com.miso.misoweather.databinding.FragmentSurveyBinding
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@ActivityRetainedScoped
class SurveyFragment @Inject constructor() : Fragment() {
    private lateinit var binding: FragmentSurveyBinding
    private lateinit var activity: ChatMainActivity
    private val viewModel by activityViewModels<ChatMainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        activity = getActivity() as ChatMainActivity
        setupData()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding =
            DataBindingUtil.inflate(layoutInflater, R.layout.fragment_survey, container, false)
        binding.viewModel = viewModel
        binding.lifecycleOwner = this
        return binding.root
    }

    private fun setupData() {
        if (viewModel.surveyItems.value.isNullOrEmpty()) {
            lifecycleScope.launch { viewModel.setupSurveyItems(activity) }
        }
    }

}