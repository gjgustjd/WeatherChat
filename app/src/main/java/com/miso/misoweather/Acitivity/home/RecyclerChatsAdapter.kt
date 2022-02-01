package com.miso.misoweather.Acitivity.home

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.miso.misoweather.R
import com.miso.misoweather.model.DTO.CommentList.Comment

class RecyclerChatsAdapter(var context: Context, var comments: List<Comment>) :
    RecyclerView.Adapter<RecyclerChatsAdapter.Holder>() {

    var viewHolders: ArrayList<Holder> = ArrayList()

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.nickname.text = comments.get(position).nickname
        holder.comment.text = comments.get(position).content
        holder.time.text =
            comments.get(position).createdAt.split("T")[1].split(".")[0].substring(0, 5)
//        holder.nickname.text = comments.get(position).
        viewHolders.add(holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.list_item_chat, parent, false)
        return Holder(view)
    }

    class Holder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var nickname = itemView.findViewById<TextView>(R.id.txt_nickname)
        var comment = itemView.findViewById<TextView>(R.id.txt_message)
        var time = itemView.findViewById<TextView>(R.id.txt_time)
        var thumbnail = itemView.findViewById<ImageView>(R.id.img_thumbnail)
    }

}