package com.miso.misoweather.activity.chatmain

import android.animation.ObjectAnimator
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.databinding.BindingAdapter
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.miso.misoweather.R
import com.miso.misoweather.activity.home.RecyclerChatsAdapter
import com.miso.misoweather.fragment.commentFragment.CommentsFragment
import com.miso.misoweather.fragment.surveyFragment.RecyclerSurveysAdapter
import com.miso.misoweather.fragment.surveyFragment.SurveyFragment
import com.miso.misoweather.model.dto.commentList.CommentListResponseDto
import kotlinx.coroutines.launch

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
        surveyItems?.let {
            view.adapter = RecyclerSurveysAdapter(view.context, surveyItems)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @BindingAdapter("refreshAction")
    @JvmStatic
    fun setRefreshAction(layout: SwipeRefreshLayout, fragment: CommentsFragment?) {
        fragment?.let {
            layout.setOnRefreshListener {
                it.setupCommentList(null)
                layout.isRefreshing = false
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @BindingAdapter("commentHint")
    @JvmStatic
    fun setCommentHint(edt: EditText, fragment: CommentsFragment?) {
        fragment?.let {
            edt.hint = "오늘 날씨에 대한 " +
                    "${fragment.activity.getBigShortScale(fragment.viewModel.bigScaleRegion)}의" +
                    " ${fragment.viewModel.nickname}님의 느낌은 어떠신가요?"
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @BindingAdapter("commentRecycler", "lifecycleOwner")
    @JvmStatic
    fun setCommentRecycler(
        view: RecyclerView,
        commentListResponseDto: CommentListResponseDto?,
        activity: ChatMainActivity?
    ) {
        commentListResponseDto?.let { it ->
            val recyclerChatAdapter = RecyclerChatsAdapter(
                it.data.commentList,
                true
            )
            recyclerChatAdapter.currentBindedPosition.observe(activity!!) { position ->
                if (position == recyclerChatAdapter.comments.size - 1) {
                    activity.lifecycleScope.launch {
                        recyclerChatAdapter.apply {
                            comments += activity.viewModel
                                .getCommentList(
                                    getCommentItem(position).id, 10
                                ).body()!!.data.commentList
                            notifyDataSetChanged()
                        }
                    }
                }
            }
            view.adapter = recyclerChatAdapter
            view.addOnScrollListener(object : RecyclerView.OnScrollListener() {
                override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                    val lastVisibleItemPosition =
                        ((view.layoutManager) as LinearLayoutManager).findLastVisibleItemPosition()
                    val itemTotalCount = view.adapter!!.itemCount - 1
                    if (lastVisibleItemPosition == itemTotalCount) {
                        Log.i("Paging", "페이징")
                    }
                }
            })
        }
    }
}