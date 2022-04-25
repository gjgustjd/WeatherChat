package com.miso.misoweather.fragment.surveyFragment

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.activity.chatmain.ChatMainActivity
import com.miso.misoweather.activity.chatmain.ChatMainViewModel
import com.miso.misoweather.activity.chatmain.SurveyItem
import com.miso.misoweather.databinding.FragmentSurveyBinding
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.launch
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
@ActivityRetainedScoped
class SurveyFragment @Inject constructor() : Fragment() {
    private lateinit var binding: FragmentSurveyBinding
    private lateinit var recyclerSurvey: RecyclerView
    private lateinit var recyclerSurveysAdapter: RecyclerSurveysAdapter
    private lateinit var currentLocation: String
    private lateinit var bigShortScale: String
    private lateinit var activity: ChatMainActivity
    private val viewModel by activityViewModels<ChatMainViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSurveyBinding.inflate(layoutInflater)
        activity = getActivity() as ChatMainActivity
        bigShortScale = activity.selectedRegion
        initializeView()
        setupData()
    }

    private fun setupData() {
        if (viewModel.surveyItems.value.isNullOrEmpty()) {
            lifecycleScope.launch { viewModel.setupSurveyItems(activity) }
        }
    }

    private fun initializeView() {
        currentLocation = activity.selectedRegion
        recyclerSurvey = binding.recyclerSurveys
        viewModel.surveyItems.observe(this) {
            if (it.size > 0)
                setupRecyclerSurveys(it)
        }
    }

    private fun setupRecyclerSurveys(surveyItems: ArrayList<SurveyItem>) {
        try {
            recyclerSurveysAdapter = RecyclerSurveysAdapter(activity, surveyItems)
            recyclerSurvey.apply {
                adapter = recyclerSurveysAdapter
                layoutManager = LinearLayoutManager(activity.baseContext)
            }
        } catch (e: Exception) {
            e.printStackTrace()
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

}