package com.miso.misoweather.Acitivity.login

import android.animation.Animator
import android.animation.TimeInterpolator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.viewpager2.widget.ViewPager2
import com.kakao.sdk.auth.model.OAuthToken
import com.kakao.sdk.user.UserApiClient
import com.kakao.sdk.user.model.AccessTokenInfo
import com.miso.misoweather.Acitivity.home.HomeActivity
import com.miso.misoweather.Acitivity.login.viewPagerFragments.*
import com.miso.misoweather.Acitivity.selectRegion.SelectRegionActivity
import com.miso.misoweather.Dialog.GeneralConfirmDialog
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ActivityLoginBinding
import com.miso.misoweather.model.DTO.GeneralResponseDto
import com.miso.misoweather.model.DTO.LoginRequestDto
import com.miso.misoweather.model.MisoRepository
import com.miso.misoweather.model.TransportManager
import com.rd.PageIndicatorView


class LoginActivity : MisoActivity() {
    lateinit var binding: ActivityLoginBinding
    lateinit var viewpager_onboarding: ViewPager2
    lateinit var pageIndicatorView: PageIndicatorView
    lateinit var repository: MisoRepository
    val onBoardFragmentList =
        listOf(
            OnBoardInitFragment(),
            OnBoardApparellFragment(),
            OnBoardFoodFragment(),
            OnBoardLocationFragment(),
            OnBoardChatFragment()
        )
    var isCheckValid = false
    var currentPosition = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initializeView()
    }

    @SuppressLint("LongLogTag")
    fun initializeView() {
        fun initializePageIndicatorView() {
            try {
                pageIndicatorView = binding.pageIndicatorView
                pageIndicatorView.setCount(onBoardFragmentList.size)
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
                viewpager_onboarding.adapter =
                    ViewPagerFragmentAdapter(
                        this,
                        onBoardFragmentList
                    )
                viewpager_onboarding.orientation = ViewPager2.ORIENTATION_HORIZONTAL
            }
            try {
                initializeViewPager()
                Thread(PagerRunnable()).start()
                matchPagerIndicator()
            } catch (e: Exception) {
                Log.i("initializeViewPager", e.stackTraceToString())
            }
        }

        repository = MisoRepository.getInstance(applicationContext)
        checkTokenValid()
        setupViewPagerAndIndicator()
        binding.clBtnKakaoLogin.setOnClickListener {
            if (hasValidToken())
                checkRegistered()
            else
                kakaoLogin()
        }
    }

    fun hasValidToken(): Boolean {
        return (isCheckValid &&
                !getPreference("socialId").isNullOrBlank() &&
                !getPreference("socialType").isNullOrBlank())
    }

    fun checkTokenValid() {
        if (UserApiClient.instance.isKakaoTalkLoginAvailable(this@LoginActivity)) {
            UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
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

    fun showDialogForInstallingKakaoTalk() {
        fun goToStoreForInstallingKakaoTalk() {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.addCategory(Intent.CATEGORY_DEFAULT)
            intent.data = Uri.parse("market://details?id=com.kakao.talk")
            startActivity(intent)
        }
        GeneralConfirmDialog(
            this,
            {
                goToStoreForInstallingKakaoTalk()
            }, "로그인하려면 카카오톡 설치가 필요합니다.\n설치하시겠습니까?"
        )
            .show(supportFragmentManager, "generalConfirmDialog")
    }

    fun showDialogForLoginKakaoTalk() {
        fun launchKakaoTalk() {
            val intent =
                packageManager.getLaunchIntentForPackage("com.kakao.talk")
            intent!!.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            startActivity(intent)
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

    fun loginWithKakaoTalk() {
        UserApiClient.instance.loginWithKakaoTalk(this@LoginActivity) { token, error ->
            if (error != null) {
                Log.e("miso", "로그인 실패", error)
                showDialogForLoginKakaoTalk()
            } else if (token != null) {
                Log.i("miso", "로그인 성공 ${token.accessToken}")
                try {
                    UserApiClient.instance.accessTokenInfo { tokenInfo, error ->
                        if (error != null) {
                            Log.i("token", "토큰 정보 보기 실패", error)
                            Toast.makeText(this, "로그인 진행 중 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show()
                        } else if (tokenInfo != null) {
                            Log.i("token", "토큰 정보 보기 성공" + "\n회원번호:${tokenInfo.id}")
                            saveTokenInfo(token, tokenInfo)
                            issueMisoToken()
                        }
                    }
                } catch (e: Exception) {
                    Log.i("kakaoLogin", e.stackTraceToString())
                    Toast.makeText(this, "로그인 진행 중 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun saveTokenInfo(token: OAuthToken, tokenInfo: AccessTokenInfo) {
        addPreferencePair("accessToken", token.accessToken)
        addPreferencePair("socialId", tokenInfo.id.toString())
        addPreferencePair("socialType", "kakao")
        savePreferences()
    }

    fun startRegionActivity() {
        startActivity(Intent(this, SelectRegionActivity::class.java))
        transferToNext()
        finish()
    }

    fun startHomeActivity() {
        startActivity(Intent(this, HomeActivity::class.java))
        finish()
    }

    fun issueMisoToken() {
        fun removeTokenAndStartRegionActivity() {
            removePreference("misoToken")
            savePreferences()
            startRegionActivity()
        }

        repository.issueMisoToken(
            makeLoginRequestDto(),
            getPreference("accessToken")!!,
            { call, response ->
                Log.i("issueMisoToken", "성공")
                var headers = response.headers()
                var serverToken = headers.get("servertoken")!!
                addPreferencePair("misoToken", serverToken!!)
                savePreferences()
                startHomeActivity()
            },
            { call, response ->
                Log.i("issueMisoToken", "실패")
                if (response.errorBody()!!.source().toString().contains("UNAUTHORIZED"))
                    kakaoLogin()
                else if (response.errorBody()!!.source().toString().contains("NOT_FOUND")) {
                    startRegionActivity()
                } else {
                    Log.i("issueMisoToken", response.errorBody()!!.source().toString())
                    Toast.makeText(this, "로그인 토큰 발급 중 문제가 발생하였습니다.", Toast.LENGTH_SHORT).show()
                }
            },
            { call, t ->
                Log.i("결과", "실패 : $t")
                removeTokenAndStartRegionActivity()
            })
    }

    fun makeLoginRequestDto(): LoginRequestDto {
        var loginRequestDto = LoginRequestDto(
            getPreference("socialId"),
            getPreference("socialType")
        )
        return loginRequestDto
    }

    fun checkRegistered() {
        repository.checkRegistered(
            getPreference("socialId")!!,
            getPreference("socialType")!!,
            { call, response ->
                issueMisoToken()
            },
            { call, response ->
                startRegionActivity()
            },
            { call, throwable ->
                startRegionActivity()
            })
    }


    inner class PagerRunnable : Runnable {
        override fun run() {
            while (true) {
                Thread.sleep(3000)
                Handler(Looper.getMainLooper()) {
                    fun setPage() {
                        fun ViewPager2.setCurrentItem(
                            item: Int,
                            duration: Long,
                            interpolator: TimeInterpolator = AccelerateDecelerateInterpolator(),
                            pagePxWidth: Int = width, // Default value taken from getWidth() from ViewPager2 view
                            pagePxHeight: Int = height
                        ) {
                            val pxToDrag: Int =
                                if (orientation == ViewPager2.ORIENTATION_HORIZONTAL)
                                    pagePxWidth * (item - currentItem)
                                else
                                    pagePxHeight * (item - currentItem)

                            val animator = ValueAnimator.ofInt(0, pxToDrag)
                            var previousValue = 0
                            animator.addUpdateListener { valueAnimator ->
                                val currentValue = valueAnimator.animatedValue as Int
                                val currentPxToDrag = (currentValue - previousValue).toFloat()
                                fakeDragBy(-currentPxToDrag)
                                previousValue = currentValue
                            }
                            animator.addListener(object : Animator.AnimatorListener {
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
                            animator.interpolator = interpolator
                            animator.duration = duration
                            animator.start()
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