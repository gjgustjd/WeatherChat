package com.miso.misoweather.activity.home

import android.os.Build
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.annotation.RequiresApi
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.databinding.ListItemChatBinding
import com.miso.misoweather.model.dto.commentList.Comment

@RequiresApi(Build.VERSION_CODES.O)
class RecyclerChatsAdapter(
    var comments: List<Comment>,
    private val isCommentsFragment: Boolean,
) :
    RecyclerView.Adapter<RecyclerChatsAdapter.Holder>() {
    val currentBindedPosition = MutableLiveData<Int>()

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

        holder.bind(comments[position])
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat, parent, false)
        return Holder(ListItemChatBinding.bind(view))
    }

    inner class Holder(val binding: ListItemChatBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: Comment) {
            binding.comment = comment
            binding.isCommentFragment = isCommentsFragment
        }
    }

}