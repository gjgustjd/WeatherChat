package com.miso.misoweather.Acitivity.chatmain

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.weatherdetail.WeatherDetailActivity
import com.miso.misoweather.Fragment.commentFragment.CommentsFragment
import com.miso.misoweather.Fragment.surveyFragment.SurveyFragment
import com.miso.misoweather.R
import com.miso.misoweather.databinding.ActivityChatMainBinding

@RequiresApi(Build.VERSION_CODES.M)
class ChatMainActivity : MisoActivity() {
    lateinit var binding: ActivityChatMainBinding
    lateinit var btn_back: ImageButton
    lateinit var btnSurvey: Button
    lateinit var btnChat: Button
     lateinit var selectedRegion:String
    lateinit var previousActivity: String
    lateinit var goToPreviousActivity: () -> Unit

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
    }

    fun initializeViews() {
        selectedRegion = intent.getStringExtra("region")?:getBigShortScale(getPreference("bigScale")!!)
        previousActivity = intent.getStringExtra("previousActivity")?:""
        btnSurvey = binding.btnSurvey
        btnChat = binding.btnChats
        Log.i("misoToken",getPreference("misoToken")!!);

        when (previousActivity) {
            "Weather" -> goToPreviousActivity =
                { startActivity(Intent(this, WeatherDetailActivity::class.java)) }
            else -> goToPreviousActivity =
                { startActivity(Intent(this, HomeActivity::class.java)) }
        }

        btnSurvey.setOnClickListener()
        {
            btnChat.setBackgroundColor(Color.TRANSPARENT)
            btnChat.setTextColor(getColor(R.color.textBlack))
            btnSurvey.background = resources.getDrawable(R.drawable.toggle_track_background)
            btnSurvey.setTextColor(Color.WHITE)
           setupFragment(SurveyFragment())
        }
        btnChat.setOnClickListener()
        {
            btnSurvey.setBackgroundColor(Color.TRANSPARENT)
            btnSurvey.setTextColor(getColor(R.color.textBlack))
            btnChat.background = resources.getDrawable(R.drawable.toggle_track_background)
            btnChat.setTextColor(Color.WHITE)
            setupFragment(CommentsFragment())
        }
        btn_back = binding.imgbtnBack
        btn_back.setOnClickListener()
        {
            goToPreviousActivity()
            transferToBack()
            finish()
        }
        setupFragment(SurveyFragment())
    }

    fun setupFragment(fragment: Fragment)
    {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentLayout,fragment)
            .commit()
    }
}