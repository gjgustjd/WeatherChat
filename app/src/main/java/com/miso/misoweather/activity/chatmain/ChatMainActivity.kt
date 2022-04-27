package com.miso.misoweather.activity.chatmain

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.lifecycle.MutableLiveData
import com.miso.misoweather.activity.home.HomeActivity
import com.miso.misoweather.activity.updateRegion.UpdateRegionActivity
import com.miso.misoweather.activity.weatherdetail.WeatherDetailActivity
import com.miso.misoweather.fragment.commentFragment.CommentsFragment
import com.miso.misoweather.fragment.surveyFragment.SurveyFragment
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityChatMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class ChatMainActivity : MisoActivity() {
    val viewModel: ChatMainViewModel by viewModels()

    @Inject
    lateinit var surveyFragment: SurveyFragment

    @Inject
    lateinit var commentsFragment: CommentsFragment
    private lateinit var binding: ActivityChatMainBinding
    val currentFragment = MutableLiveData<Fragment>()
    private val goToPreviousActivity: () -> Unit = {
        when (intent.getStringExtra("previousActivity") ?: "") {
            "Weather" -> {
                startActivity(Intent(this, WeatherDetailActivity::class.java))
                transferToBack()
                finish()
            }
            else -> {
                startActivity(Intent(this, HomeActivity::class.java))
                transferToBack()
                finish()
            }
        }
    }
    val selectedRegion by lazy {
        val surveyRegion = viewModel.surveyRegion

        if (surveyRegion.isNullOrBlank())
            getBigShortScale(viewModel.bigScaleRegion)
        else surveyRegion
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_chat_main)
        binding.activity = this
        binding.viewModel = viewModel
        binding.lifecycleOwner = this

        setupFragment(surveyFragment)
    }

    fun goToUpdateRegionActivity() {
        val intent = Intent(this, UpdateRegionActivity::class.java)
        intent.putExtra("region", selectedRegion)
        startActivity(intent)
        transferToNext()
        finish()
    }

    fun setSurveyFragment() {
        if (!currentFragment.value!!.equals(surveyFragment)) {
            setupFragment(surveyFragment)
        }
    }

    fun setCommentsFragment() {
        if (!currentFragment.value!!.equals(commentsFragment)) {
            setupFragment(commentsFragment)
        }
    }

    override fun doBack() {
        viewModel.removeSurveyRegion()
        goToPreviousActivity()
    }

    private fun setupFragment(fragment: Fragment) {
        currentFragment.value = fragment
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.fragmentLayout, fragment)
            .commit()
    }

}