package com.miso.misoweather.Acitivity.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.Acitivity.login.viewPagerFragments.OnBoardChatFragment
import com.miso.misoweather.Fragment.commentFragment.CommentsFragment
import com.miso.misoweather.R
import com.miso.misoweather.databinding.ListItemChatBinding
import com.miso.misoweather.model.DTO.CommentList.Comment
import org.w3c.dom.Text

class RecyclerChatsAdapter(
    var context: Context,
    var comments: List<Comment>,
    var isCommentsFragment: Boolean
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
        holder.time.text =
            comments.get(position).createdAt.split("T")[1].split(".")[0].substring(0, 5)
        holder.emoji.text = comments.get(position).emoji
        if (isCommentsFragment)
            holder.background.setBackgroundResource(R.drawable.unit_background)
        viewHolders.add(holder)
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