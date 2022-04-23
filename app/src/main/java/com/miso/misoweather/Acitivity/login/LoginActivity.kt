package com.miso.misoweather.Acitivity.login

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.viewpager2.widget.ViewPager2
import com.kakao.sdk.user.UserApiClient
import com.kakao.usermgmt.response.model.User
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.login.viewPagerFragments.*
import com.miso.misoweather.Acitivity.selectRegion.SelectRegionActivity
import com.miso.misoweather.Dialog.GeneralConfirmDialog
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityLoginBinding
import com.rd.PageIndicatorView
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response

@AndroidEntryPoint
@RequiresApi(Build.VERSION_CODES.O)
class LoginActivity : MisoActivity() {
    private val viewModel: LoginViewModel by viewModels()
    private lateinit var binding: ActivityLoginBinding
    private lateinit var viewpager_onboarding: ViewPager2
    private lateinit var pageIndicatorView: PageIndicatorView
    private lateinit var accessToken: String
    private lateinit var socialId: String
    private lateinit var socialType: String
    private val onBoardFragmentList =
        listOf(
            OnBoardInitFragment(),
            OnBoardApparellFragment(),
            OnBoardFoodFragment(),
            OnBoardLocationFragment(),
            OnBoardChatFragment()
        )
    private var isCheckValid = false
    private var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeView()
    }

    @SuppressLint("LongLogTag")
    private fun initializeView() {
        fun initializePageIndicatorView() {
            try {
                pageIndicatorView = binding.pageIndicatorView
                pageIndicatorView.count = onBoardFragmentList.size
            } catch (e: Exception) {
                Log.i("initializePageIndicatorView", e.stackTraceToString())
                throw Exception("pageIndicatorView is null")
            }
        }

        fun matchPagerIndicator() {
            try {
                initializePageIndicatorView()
                viewpager_onboarding.registerOnPageChangeCallback(object :
                    ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        try {
                            super.onPageSelected(position)
                            pageIndicatorView.setSelected(position)
                        } catch (e: Exception) {
                            Log.i("viewpate_onboarding.onPageSelected", e.stackTraceToString())
                        }
                    }
                })
            } catch (e: Exception) {
                Log.i("matchPagerIndicator", e.stackTraceToString())
            }
        }

        fun setupViewPagerAndIndicator() {
            fun initializeViewPager() {
                viewpager_onboarding = binding.viewPagerOnBoarding
                viewpager_onboarding.apply {
                    adapter =
                        ViewPagerFragmentAdapter(
                            this@LoginActivity,
                            onBoardFragmentList
                        )
                    orientation = ViewPager2.ORIENTATION_HORIZONTAL
                }
            }
            try {
                initializeViewPager()
                Thread(PagerRunnable()).start()
                matchPagerIndicator()
            } catch (e: Exception) {
                Log.i("initializeViewPager", e.stackTraceToString())
            }
        }

        checkTokenValid()
        setupViewPagerAndIndicator()
        binding.clBtnKakaoLogin.setOnClickListener {
            if (hasValidToken())
                checkRegistered()
            else
                kakaoLogin()
        }
        viewModel.accessToken.observe(this)
        {
            accessToken = it
        }
        viewModel.socialId.observe(this)
        {
            socialId = it
        }
        viewModel.socialType.observe(this)
        {
            socialType = it
        }
    }

    private fun hasValidToken(): Boolean {
        return (isCheckValid &&
                !socialId.isNullOrBlank() &&
                !socialType.isNullOrBlank())
    }

    private fun checkTokenValid() {
        UserApiClient.instance.apply {
            if (isKakaoTalkLoginAvailable(this@LoginActivity)) {
                accessTokenInfo { tokenInfo, error ->
                    if (error != null) {
                        Log.i("token", "토큰 정보 보기 실패", error)
                        isCheckValid = false
                    } else if (tokenInfo != null) {
                        Log.i(
                            "token", "토큰 정보 보기 성공" +
                                    "\n회원번호:${tokenInfo.id}"
                        )
                        isCheckValid = true
                    }
                }
            } else
                isCheckValid = false
        }
    }

    private fun showDialogForInstallingKakaoTalk() {
        fun goToStoreForInstallingKakaoTalk(generalConfirmDialog: GeneralConfirmDialog) {
            try {
                val intent = Intent(Intent.ACTION_VIEW)
                intent.apply {
                    addCategory(Intent.CATEGORY_DEFAULT)
                    data = Uri.parse("market://details?id=com.kakao.talk")
                }
                startActivity(intent)
            } catch (e: Exception) {
                Log.e("instaillingKakaoTalk", e.stackTraceToString())
                Toast.makeText(this, "카카오톡 설치에 실패했습니다.", Toast.LENGTH_SHORT).show()
                generalConfirmDialog.dismiss()
            }
        }

        lateinit var generalConfirmDialog: GeneralConfirmDialog

        generalConfirmDialog = GeneralConfirmDialog(
            this,
            {
                goToStoreForInstallingKakaoTalk(generalConfirmDialog)
            }, "로그인하려면 카카오톡 설치가 필요합니다.\n설치하시겠습니까?"
        )

        generalConfirmDialog.show(supportFragmentManager, "generalConfirmDialog")
    }

    private fun showDialogForLoginKakaoTalk() {
        fun launchKakaoTalk() {
            try {
                val intent =
                    packageManager.getLaunchIntentForPackage("com.kakao.talk")
                intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            } catch (e: Exception) {

            }
        }
        GeneralConfirmDialog(
            this,
            {
                launchKakaoTalk()
            },
            "카카오톡에 로그인되지 않았습니다.\n실행하시겠습니까?"
        ).show(supportFragmentManager, "generalConfirmDialog")
    }

    fun kakaoLogin() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@LoginActivity)) {
            loginWithKakaoTalk()
        } else {
            showDialogForInstallingKakaoTalk()
        }
    }

    private fun loginWithKakaoTalk() {
        UserApiClient.instance.apply {
            loginWithKakaoTalk(this@LoginActivity) { token, error ->
                try {
                    if (error != null) {
                        Log.e("miso", "로그인 실패", error)
                        showDialogForLoginKakaoTalk()
                    } else if (token != null) {
                        Log.i("miso", "로그인 성공 ${token.accessToken}")
                        accessTokenInfo { tokenInfo, error ->
                            if (error != null) {
                                Log.i("token", "토큰 정보 보기 실패", error)
                                Toast.makeText(
                                    this@LoginActivity,
                                    "로그인 진행 중 문제가 발생하였습니다.",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            } else if (tokenInfo != null) {
                                Log.i("token", "토큰 정보 보기 성공" + "\n회원번호:${tokenInfo.id}")
                                viewModel.saveTokenInfo(token, tokenInfo)
                                issueMisoToken()
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.i("kakaoLogin", e.stackTraceToString())
                    Toast.makeText(this@LoginActivity, "로그인 진행 중 문제가 발생하였습니다.", Toast.LENGTH_SHORT)
                        .show()
                }
            }
        }
    }


    private fun startRegionActivity() {
        startActivity(Intent(this, SelectRegionActivity::class.java))
        transferToNext()
        finish()
    }

    private fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    private fun issueMisoToken() {
        Log.i("issueMisoToekn","실행됨")
        viewModel.issueMisoToken()
        viewModel.issueMisoTokenResponse.observe(this) {
            if (it is Response<*>) {
                if (it.isSuccessful) {
                    startHomeActivity()
                } else {
                    Log.i("issueMisoToken", "실패")
                    var errorString = it.errorBody()!!.source().toString()
                    if (errorString.contains("UNAUTHORIZED"))
                        kakaoLogin()
                    else if (errorString.contains("NOT_FOUND")) {
                        startRegionActivity()
                    } else {
                        Log.i("issueMisoToken", errorString)
                        Toast.makeText(this, "로그인 토큰 발급 중 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show()
                    }
                }
            } else {
                startRegionActivity()
            }
        }
    }


    private fun checkRegistered() {
        viewModel.checkRegistered()
        viewModel.checkRegistered.observe(this) {
            if (it!!)
                issueMisoToken()
            else
                startRegionActivity()
        }
    }


    inner class PagerRunnable : Runnable {
        override fun run() {
            while (true) {
                Thread.sleep(3000)
                Handler(Looper.getMainLooper()) {
                    fun setPage() {
                        fun ViewPager2.setCurrentItem(
                            item: Int,
                            aDuration: Long,
                            aInterpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
                            pagePxWidth: Int = width, // Default value taken from getWidth() from ViewPager2 view
                            pagePxHeight: Int = height
                        ) {
                            val pxToDrag: Int =
                                if (orientation == ViewPager2.ORIENTATION_HORIZONTAL)
                                    pagePxWidth * (item - currentItem)
                                else
                                    pagePxHeight * (item - currentItem)

                            var previousValue = 0
                            ValueAnimator.ofInt(0, pxToDrag).apply {
                                addUpdateListener { valueAnimator ->
                                    val currentValue = valueAnimator.animatedValue as Int
                                    val currentPxToDrag = (currentValue - previousValue).toFloat()
                                    fakeDragBy(-currentPxToDrag)
                                    previousValue = currentValue
                                }
                                addListener(object : Animator.AnimatorListener {
                                    override fun onAnimationStart(animation: Animator?) {
                                        beginFakeDrag()
                                    }

                                    override fun onAnimationEnd(animation: Animator?) {
                                        endFakeDrag()
                                    }

                                    override fun onAnimationCancel(animation: Animator?) { /* Ignored */
                                    }

                                    override fun onAnimationRepeat(animation: Animator?) { /* Ignored */
                                    }
                                })
                                interpolator = aInterpolator
                                duration = aDuration
                                start()
                            }
                        }
                        if (currentPosition == 5) currentPosition = 0
                        viewpager_onboarding.setCurrentItem(currentPosition, 500)
                        currentPosition += 1
                    }
                    setPage()
                    true
                }.sendEmptyMessage(0)
            }
        }
    }
}