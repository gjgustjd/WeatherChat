package com.miso.misoweather.Acitivity.chatmain

import android.animation.ObjectAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.updateRegion.UpdateRegionActivity
import com.miso.misoweather.Acitivity.weatherdetail.WeatherDetailActivity
import com.miso.misoweather.Fragment.commentFragment.CommentsFragment
import com.miso.misoweather.Fragment.surveyFragment.SurveyFragment
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityChatMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject


@RequiresApi(Build.VERSION_CODES.O)
@AndroidEntryPoint
class ChatMainActivity : MisoActivity() {
    private val viewModel: ChatMainViewModel by viewModels()

    @Inject
    lateinit var surveyFragment: SurveyFragment

    @Inject
    lateinit var commentsFragment: CommentsFragment

    private lateinit var binding: ActivityChatMainBinding
    private lateinit var btn_back: ImageButton
    private lateinit var btnSurvey: Button
    private lateinit var btnChat: Button
    private lateinit var txtLocation: TextView
    private lateinit var txtSurveyBtn: TextView
    private lateinit var txtChatBtn: TextView
    lateinit var selectedRegion: String
    private lateinit var locationLayout: ConstraintLayout
    private lateinit var previousActivity: String
    private lateinit var currentFragment: Fragment
    private lateinit var goToPreviousActivity: () -> Unit
    private lateinit var misoToken: String
    private lateinit var surveyRegion: String
    private lateinit var bigScale: String
    private lateinit var defaultRegionId: String
    private var isAllInitialized = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeProperties()
    }

    private fun initializeProperties() {
        fun checkInitializedAll() {
            if (!isAllInitialized) {
                if (this::bigScale.isInitialized &&
                    this::surveyRegion.isInitialized &&
                    this::misoToken.isInitialized &&
                    this::defaultRegionId.isInitialized
                ) {
                    initializeViews()
                    isAllInitialized = true
                }
            }
        }
        viewModel.updateProperties()
        viewModel.misoToken.observe(this) {
            misoToken = it
            checkInitializedAll()
        }
        viewModel.surveyRegion.observe(this) {
            surveyRegion = it
            checkInitializedAll()
        }
        viewModel.bigScaleRegion.observe(this) {
            bigScale = it
            checkInitializedAll()
        }
        viewModel.defaultRegionId.observe(this) {
            defaultRegionId = it
            checkInitializedAll()
        }
    }

    private fun initializeViews() {
        selectedRegion =
            if (surveyRegion.isNullOrBlank())
                getBigShortScale(bigScale)
            else surveyRegion!!

        previousActivity = intent.getStringExtra("previousActivity") ?: ""
        btnSurvey = binding.btnSurvey
        btnChat = binding.btnChats
        locationLayout = binding.locationButtonLayout

        txtLocation = binding.txtLocation
        txtLocation.text = selectedRegion

        txtSurveyBtn = binding.txtSurveyBtn
        txtChatBtn = binding.txtChatBtn

        when (previousActivity) {
            "Weather" -> {
                goToPreviousActivity =
                    {
                        startActivity(Intent(this, WeatherDetailActivity::class.java))
                        transferToBack()
                        finish()
                    }
            }
            else -> goToPreviousActivity =
                {
                    startActivity(Intent(this, HomeActivity::class.java))
                    transferToBack()
                    finish()
                }
        }

        btnSurvey.setOnClickListener()
        {

            if (!currentFragment.equals(surveyFragment)) {
                setButtonPressed(btnSurvey)
                setButtonUnpressed(btnChat)
                turnLocationButton(true)
                setupFragment(surveyFragment)
            }
        }
        btnChat.setOnClickListener()
        {
            if (!currentFragment.equals(commentsFragment)) {
                setButtonPressed(btnChat)
                setButtonUnpressed(btnSurvey)
                turnLocationButton(false)
                setupFragment(commentsFragment)
            }
        }
        btn_back = binding.imgbtnBack
        btn_back.setOnClickListener()
        {
            doBack()
        }
        txtLocation.setOnClickListener()
        {
            val intent = Intent(this, UpdateRegionActivity::class.java)
            intent.putExtra("region", selectedRegion)
            startActivity(intent)
            transferToNext()
            finish()
        }
        setupFragment(surveyFragment)
    }

    private fun View.startBackgroundAlphaAnimation(fromValue: Float, toValue: Float) {
        ObjectAnimator.ofFloat(this, "alpha", fromValue, toValue).start()
    }

    private fun turnLocationButton(turnOn: Boolean) {
        if (turnOn) {
            locationLayout.visibility = View.VISIBLE
            locationLayout.startBackgroundAlphaAnimation(0f, 1f)
        } else {
            locationLayout.visibility = View.GONE
            locationLayout.startBackgroundAlphaAnimation(1f, 0f)
        }
    }

    private fun setButtonPressed(button: Button) {
        if (button.equals(btnChat)) {
            btnChat.startBackgroundAlphaAnimation(0f, 1f)
            txtChatBtn.setTextColor(Color.WHITE)
        } else if (button.equals(btnSurvey)) {
            btnSurvey.startBackgroundAlphaAnimation(0f, 1f)
            txtSurveyBtn.setTextColor(Color.WHITE)
        }
    }

    private fun setButtonUnpressed(button: Button) {
        if (button.equals(btnChat)) {
            txtChatBtn.setTextColor(getColor(R.color.textBlack))
            btnChat.startBackgroundAlphaAnimation(1f, 0f)
        } else if (button.equals(btnSurvey)) {
            txtSurveyBtn.setTextColor(getColor(R.color.textBlack))
            btnSurvey.startBackgroundAlphaAnimation(1f, 0f)
        }
    }

    override fun doBack() {
        viewModel.removeSurveyRegion()
        goToPreviousActivity()
    }

    private fun setupFragment(fragment: Fragment) {
        currentFragment = fragment
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.fragmentLayout, fragment)
            .commit()
    }

}