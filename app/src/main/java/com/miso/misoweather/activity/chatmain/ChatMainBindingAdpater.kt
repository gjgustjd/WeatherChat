package com.miso.misoweather.activity.chatmain

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Build
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.fragment.commentFragment.CommentsFragment
import com.miso.misoweather.fragment.surveyFragment.RecyclerSurveysAdapter
import com.miso.misoweather.fragment.surveyFragment.SurveyFragment
import com.miso.misoweather.fragment.surveyFragment.SurveyFragment_Factory

object ChatMainBindingAdpater {
    private fun View.startBackgroundAlphaAnimation(fromValue: Float, toValue: Float) {
        ObjectAnimator.ofFloat(this, "alpha", fromValue, toValue).start()
    }

    @BindingAdapter("surveyBtnAnimation")
    @JvmStatic
    fun setSurveyAnimation(view: View, fragment: Fragment?) {
        fragment?.let {
            if (fragment is SurveyFragment)
                view.startBackgroundAlphaAnimation(0f, 1f)
            else
                view.startBackgroundAlphaAnimation(1f, 0f)
        }
    }

    @BindingAdapter("commentsBtnAnimation")
    @JvmStatic
    fun setCommentsAnimation(view: View, fragment: Fragment?) {
        fragment?.let {
            if (fragment is CommentsFragment)
                view.startBackgroundAlphaAnimation(0f, 1f)
            else
                view.startBackgroundAlphaAnimation(1f, 0f)
        }
    }

    @BindingAdapter("surveyVisibility")
    @JvmStatic
    fun setSurveyVisibility(view: View, fragment: Fragment?) {
        fragment?.let {
            view.visibility =
                if (fragment is SurveyFragment)
                    View.VISIBLE
                else
                    View.GONE
        }
    }

    @BindingAdapter("commentsVisibility")
    @JvmStatic
    fun setCommentsVisibility(view: View, fragment: Fragment?) {
        fragment?.let {
            view.visibility =
                if (fragment is CommentsFragment)
                    View.VISIBLE
                else
                    View.GONE
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @BindingAdapter("surveyTextColor")
    @JvmStatic
    fun setSurveyTextColor(view: TextView, fragment: Fragment?) {
        fragment?.let {
            if (fragment is SurveyFragment)
                view.setTextColor(Color.WHITE)
            else
                view.setTextColor(view.context.resources.getColor(R.color.textBlack, null))
        }
    }

    @RequiresApi(Build.VERSION_CODES.M)
    @BindingAdapter("commentsTextColor")
    @JvmStatic
    fun setCommentsTextColor(view: TextView, fragment: Fragment?) {
        fragment?.let {
            if (fragment is CommentsFragment)
                view.setTextColor(Color.WHITE)
            else
                view.setTextColor(view.context.resources.getColor(R.color.textBlack, null))
        }
    }


    @BindingAdapter("recyclerSurveyItems")
    @JvmStatic
    fun setRecyclerSurveyItems(view: RecyclerView, surveyItems: ArrayList<SurveyItem>?) {
        surveyItems?.let{
            view.adapter = RecyclerSurveysAdapter(view.context, surveyItems)
        }
    }
}