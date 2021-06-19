package com.vs.meetly.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.vs.meetly.R
import com.vs.meetly.miscellaneous.Utils
import com.vs.meetly.modals.Support


class SupportAdapter(val context: Context, private val support: ArrayList<Support>) :
    RecyclerView.Adapter<SupportAdapter.SupportViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SupportViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.dev_news_item, parent, false)
        return SupportViewHolder(view)
    }

    override fun onBindViewHolder(holder: SupportViewHolder, position: Int) {
        holder.username.text = support[position].name
        holder.content.text = support[position].content
        holder.lastTime.text = Utils.getTimeAgo(support[position].date.toLong())
        Glide.with(holder.userImage.context).load(support[position].avatar).circleCrop().into(holder.userImage)
    }

    override fun getItemCount(): Int {
        return support.size
    }

    inner class SupportViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val username: TextView = itemView.findViewById(R.id.textUsername)
        val content: TextView = itemView.findViewById(R.id.textDes)
        val lastTime: TextView = itemView.findViewById(R.id.lastTime)
        val userImage: ImageView = itemView.findViewById(R.id.imageView3)

    }
}

