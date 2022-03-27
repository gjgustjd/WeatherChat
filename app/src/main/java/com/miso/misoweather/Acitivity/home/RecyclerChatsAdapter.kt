package com.miso.misoweather.Acitivity.home

import android.content.Context
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.chatmain.ChatMainViewModel
import com.miso.misoweather.R
import com.miso.misoweather.databinding.ListItemChatBinding
import com.miso.misoweather.model.DTO.CommentList.Comment
import lombok.Getter
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import javax.inject.Inject

@RequiresApi(Build.VERSION_CODES.O)
class RecyclerChatsAdapter(
    var context: Context,
    @Getter
    var comments: List<Comment>,
    var isCommentsFragment: Boolean,
) :
    RecyclerView.Adapter<RecyclerChatsAdapter.Holder>() {

    @Inject
    lateinit var viewModel: ChatMainViewModel
    var viewHolders: ArrayList<Holder> = ArrayList()
    var currentBindedPosition = MutableLiveData<Int>()

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    fun getCommentItem(position: Int): Comment {
        return comments[position]
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        currentBindedPosition.value = position
        holder.nickname.text = "${comments.get(position).bigScale}의 ${comments.get(position).nickname}"
        holder.comment.text = comments.get(position).content

        val currentTimeString =
            ZonedDateTime.now(ZoneId.of("Asia/Seoul"))
                .format(DateTimeFormatter.ofPattern("yyyy-MM-dd")).toString()

        val commentCreatedAt = comments.get(position).createdAt
        if (commentCreatedAt != null) {
            if (commentCreatedAt.contains("T")) {
                val commentDayString =
                    commentCreatedAt.split("T")[0]
                if (commentDayString.equals(currentTimeString))
                    holder.time.text =
                        commentCreatedAt.split("T")[1].split(".")[0].substring(0, 5)
                else
                    holder.time.text = commentDayString + " " +
                            commentCreatedAt.split("T")[1].split(".")[0].substring(0, 5)
            } else {
                holder.time.text = commentCreatedAt
            }
        } else {
            holder.time.text = ""
        }

        holder.emoji.text = comments.get(position).emoji

        if (isCommentsFragment)
            holder.background.setBackgroundResource(R.drawable.unit_background)

        viewHolders.add(holder)
        Log.i("onBindViewHolder", position.toString())

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