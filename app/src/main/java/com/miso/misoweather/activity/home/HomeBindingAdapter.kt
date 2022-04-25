package com.miso.misoweather.activity.home

import android.os.Build
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.databinding.BindingAdapter
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.model.dto.commentList.Comment
import com.miso.misoweather.model.dto.surveyResultResponse.SurveyResultResponseDto
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

object HomeBindingAdapter {
    @BindingAdapter("emptyLayoutVisibility")
    @JvmStatic
    fun setupVisibility(view: View, surveyResultResponseDto: SurveyResultResponseDto?) {
        surveyResultResponseDto?.let { it ->
            it.data.responseList.first { it.surveyId == 2 }.let {
                if (it.keyList.all { it.toString().isNullOrBlank() } &&
                    it.valueList.all { it.toString().isNullOrBlank() })
                    view.visibility = View.VISIBLE
                else
                    view.visibility = View.GONE
            }
        }
    }

    @BindingAdapter("ItemVisibility")
    @JvmStatic
    fun setupItemVisibility(view: View, surveyResultResponseDto: SurveyResultResponseDto?) {
        surveyResultResponseDto?.let { it ->
            it.data.responseList.first { it.surveyId == 2 }.let {
                val index = view.tag.toString().toInt()
                if (it.keyList[index].toString().isNullOrBlank() &&
                    it.valueList[index].toString().isNullOrBlank()
                )
                    view.visibility = View.GONE
                else
                    view.visibility = View.VISIBLE
            }
        }
    }

    @BindingAdapter("answer")
    @JvmStatic
    fun setAnswer(view: TextView, surveyResultResponseDto: SurveyResultResponseDto?) {
        surveyResultResponseDto?.let { it ->
            it.data.responseList.first { it.surveyId == 2 }.let {
                val index = view.tag.toString().toInt()
                view.text = it.keyList[index].toString()
            }
        }
    }

    @BindingAdapter("ratio")
    @JvmStatic
    fun setRatio(view: TextView, surveyResultResponseDto: SurveyResultResponseDto?) {
        surveyResultResponseDto?.let { it ->
            it.data.responseList.first { it.surveyId == 2 }.let {
                val index = view.tag.toString().toInt()
                view.text = "${it.valueList[index]} %"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @BindingAdapter("bindData")
    @JvmStatic
    fun setRecycerData(view: RecyclerView, commentList: List<Comment>?) {
        commentList?.let { it ->
            view.adapter = RecyclerChatsAdapter(commentList, false)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    @BindingAdapter("commentNickname")
    @JvmStatic
    fun setCommentNickName(view: TextView, comment: Comment) {
        view.text = "${comment.bigScale}의 ${comment.nickname}"
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @BindingAdapter("commentContent")
    @JvmStatic
    fun setCommentContent(view: TextView, comment: Comment) {
        view.text = comment.content
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @BindingAdapter("commentEmoji")
    @JvmStatic
    fun setCommentEmoji(view: TextView, comment: Comment) {
        view.text = comment.emoji
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @BindingAdapter("commentBackground")
    @JvmStatic
    fun setCommentBackground(view: ConstraintLayout, isCommentFragment: Boolean) {
        if (isCommentFragment)
            view.setBackgroundResource(R.drawable.unit_background)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    @BindingAdapter("commentTime")
    @JvmStatic
    fun setCommentTime(view: TextView, comment: Comment) {
        val currentTimeString =
            ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()

        val commentCreatedAt = comment.createdAt
        if (commentCreatedAt != null) {
            if (commentCreatedAt.contains("T")) {
                val commentDayString =
                    commentCreatedAt.split("T")[0]
                if (commentDayString.equals(currentTimeString))
                    view.text =
                        commentCreatedAt.split("T")[1].split(".")[0].substring(0, 5)
                else
                    view.text = commentDayString + " " +
                            commentCreatedAt.split("T")[1].split(".")[0].substring(0, 5)
            } else {
                view.text = commentCreatedAt
            }
        } else {
            view.text = ""
        }
    }
}