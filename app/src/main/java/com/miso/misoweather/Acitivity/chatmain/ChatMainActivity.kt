package com.miso.misoweather.Acitivity.chatmain

import android.animation.ArgbEvaluator
import android.animation.ObjectAnimator
import android.animation.ValueAnimator
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.updateRegion.UpdateRegionActivity
import com.miso.misoweather.Acitivity.weatherdetail.WeatherDetailActivity
import com.miso.misoweather.Fragment.commentFragment.CommentsFragment
import com.miso.misoweather.Fragment.surveyFragment.SurveyFragment
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityChatMainBinding


@RequiresApi(Build.VERSION_CODES.O)
class ChatMainActivity : MisoActivity() {
    lateinit var binding: ActivityChatMainBinding
    lateinit var btn_back: ImageButton
    lateinit var btnSurvey: Button
    lateinit var btnChat: Button
    lateinit var txtLocation: TextView
    lateinit var txtSurveyBtn: TextView
    lateinit var txtChatBtn:TextView
    lateinit var selectedRegion: String
    lateinit var locationLayout: ConstraintLayout
    lateinit var previousActivity: String
    lateinit var currentFragment: Fragment
    lateinit var goToPreviousActivity: () -> Unit
    lateinit var surveyFragment: SurveyFragment
    lateinit var commentsFragment: CommentsFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState);
        binding = ActivityChatMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeViews()
    }

    fun initializeViews() {
        surveyFragment = SurveyFragment()
        commentsFragment = CommentsFragment()
        selectedRegion =
            if (getPreference("surveyRegion").isNullOrBlank())
                getBigShortScale(getPreference("bigScale")!!)
            else getPreference("surveyRegion")!!
        previousActivity = intent.getStringExtra("previousActivity") ?: ""
        btnSurvey = binding.btnSurvey
        btnChat = binding.btnChats
        locationLayout = binding.locationButtonLayout

        txtLocation = binding.txtLocation
        txtLocation.text = selectedRegion

        txtSurveyBtn = binding.txtSurveyBtn
        txtChatBtn= binding.txtChatBtn
        Log.i("misoToken", getPreference("misoToken")!!);

        when (previousActivity) {
            "Weather" -> goToPreviousActivity =
                { startActivity(Intent(this, WeatherDetailActivity::class.java)) }
            else -> goToPreviousActivity =
                { startActivity(Intent(this, HomeActivity::class.java)) }
        }

        btnSurvey.setOnClickListener()
        {

            btnSurvey.startBackgroundAnimation(0f, 1f)
            txtChatBtn.setTextColor(getColor(R.color.textBlack))
            btnChat.startBackgroundAnimation(1f, 0f)
            txtSurveyBtn.setTextColor(Color.WHITE)
            locationLayout.visibility = View.VISIBLE
            locationLayout.startBackgroundAnimation(0f, 1f)
            setupFragment(surveyFragment)
        }
        btnChat.setOnClickListener()
        {
            btnChat.startBackgroundAnimation(0f, 1f)
            txtSurveyBtn.setTextColor(getColor(R.color.textBlack))
            btnSurvey.startBackgroundAnimation(1f,0f)
            txtChatBtn.setTextColor(Color.WHITE)
            locationLayout.visibility = View.GONE
            locationLayout.startBackgroundAnimation(1f, 0f)
            setupFragment(commentsFragment)
        }
        btn_back = binding.imgbtnBack
        btn_back.setOnClickListener()
        {
            doBack()
        }
        txtLocation.setOnClickListener()
        {
            var intent = Intent(this, UpdateRegionActivity::class.java)
            intent.putExtra("region", selectedRegion)
            startActivity(intent)
            transferToNext()
            finish()
        }
        setupFragment(surveyFragment)
    }

    fun View.startBackgroundAnimation(fromValue:Float, toValue: Float) {
        ObjectAnimator.ofFloat(this,"alpha",fromValue,toValue).start()
    }

    override fun doBack() {
        removePreference("surveyRegion")
        savePreferences()
        goToPreviousActivity()
        transferToBack()
        finish()
    }

    fun setupFragment(fragment: Fragment) {
        currentFragment = fragment
        supportFragmentManager.beginTransaction()
            .setCustomAnimations(R.anim.fade_in, R.anim.fade_out)
            .replace(R.id.fragmentLayout, fragment)
            .commit()
    }
}