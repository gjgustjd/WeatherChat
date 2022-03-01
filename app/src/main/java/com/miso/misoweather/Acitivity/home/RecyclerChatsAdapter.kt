package com.miso.misoweather.Acitivity.home

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.chatmain.ChatMainActivity
import com.miso.misoweather.Acitivity.login.viewPagerFragments.OnBoardChatFragment
import com.miso.misoweather.Fragment.commentFragment.CommentsFragment
import com.miso.misoweather.R
import com.miso.misoweather.common.MisoActivity
import com.miso.misoweather.databinding.ListItemChatBinding
import com.miso.misoweather.model.DTO.CommentList.Comment
import com.miso.misoweather.model.DTO.CommentList.CommentListResponseDto
import com.miso.misoweather.model.interfaces.MisoWeatherAPI
import org.w3c.dom.Text
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class RecyclerChatsAdapter(
    var context: Context,
    var comments: List<Comment>,
    var isCommentsFragment: Boolean,
    var isInitialized: Boolean = false
) :
    RecyclerView.Adapter<RecyclerChatsAdapter.Holder>() {

    var viewHolders: ArrayList<Holder> = ArrayList()

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.nickname.text = comments.get(position).nickname
        holder.comment.text = comments.get(position).content

        val currentTimeString =
            LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()
        val commentDayString = comments.get(position).createdAt.split("T")[0]
        if (commentDayString.equals(currentTimeString))
            holder.time.text =
                comments.get(position).createdAt.split("T")[1].split(".")[0].substring(0, 5)
        else
            holder.time.text = commentDayString + " " +
                    comments.get(position).createdAt.split("T")[1].split(".")[0].substring(0, 5)

        holder.emoji.text = comments.get(position).emoji
        if (isCommentsFragment) {
            holder.background.setBackgroundResource(R.drawable.unit_background)
            if (position == comments.size - 1) {
                addCommentList(comments[position].id, 5)
            }
        }
        viewHolders.add(holder)
        Log.i("onBindViewHolder", position.toString())

    }

    fun addCommentList(commentId: Int?, size: Int) {
        val retrofit = Retrofit.Builder()
            .baseUrl(MisoActivity.MISOWEATHER_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        val api = retrofit.create(MisoWeatherAPI::class.java)
        val callgetCommentList = api.getCommentList(commentId, size)

        callgetCommentList.enqueue(object : Callback<CommentListResponseDto> {
            override fun onResponse(
                call: Call<CommentListResponseDto>,
                response: Response<CommentListResponseDto>
            ) {
                try {
                    Log.i("결과", "성공")
                    var commentListResponseDto = response.body()!!
                    if (commentId == null)
                        comments = commentListResponseDto.data.commentList
                    else
                        comments += commentListResponseDto.data.commentList
                    notifyDataSetChanged()
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }

            override fun onFailure(call: Call<CommentListResponseDto>, t: Throwable) {
                Log.i("결과", "실패 : $t")
            }
        })
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat, parent, false)
        return Holder(ListItemChatBinding.bind(view))
    }

    class Holder(itemView: ListItemChatBinding) : RecyclerView.ViewHolder(itemView.root) {
        var background = itemView.background
        var nickname = itemView.txtNickname
        var comment = itemView.txtMessage
        var time = itemView.txtTime
        var emoji = itemView.txtEmoji
    }

}