package com.vs.meetly.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.vs.meetly.R
import com.vs.meetly.modals.Meeting


class MeetingAdapter(val context: Context, private val meeting: List<Meeting>) :
    RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.meeting_item, parent, false)
        return MeetingViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeetingViewHolder, position: Int) {
        holder.meetingdate.text = meeting[position].date
        holder.textContent.text = meeting[position].content
        holder.meetingtime.text = meeting[position].time
    }

    override fun getItemCount(): Int {
        return meeting.size
    }

    inner class MeetingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val meetingdate: TextView = itemView.findViewById(R.id.meetingdate)
        val textContent: TextView = itemView.findViewById(R.id.textContent)
        val meetingtime : TextView = itemView.findViewById(R.id.meetingtime)
    }
}

