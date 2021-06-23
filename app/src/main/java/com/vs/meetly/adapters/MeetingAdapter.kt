package com.vs.meetly.adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView

import com.vs.meetly.R
import com.vs.meetly.miscellaneous.ColorPicker
import com.vs.meetly.modals.Meeting


class MeetingAdapter(val context: Context, private val meeting: List<Meeting>, private val listener: IMeetingRVAdapter) :
    RecyclerView.Adapter<MeetingAdapter.MeetingViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MeetingViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.meeting_item, parent, false)
        return MeetingViewHolder(view)
    }

    override fun onBindViewHolder(holder: MeetingViewHolder, position: Int) {
        holder.meetingdate.text = meeting[position].date
        holder.textContent.text = meeting[position].title
        holder.meetingtime.text = meeting[position].time
        holder.linearLayout.setBackgroundColor(Color.parseColor(ColorPicker.getColor()))
        holder.deleteMeeting.setOnClickListener {
            listener.onItemClicked(meeting[position])
        }
        holder.cardViewMeeting.setOnClickListener {
            listener.getIntoActivity(meeting[position])
        }
    }

    override fun getItemCount(): Int {
        return meeting.size
    }

    inner class MeetingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val meetingdate: TextView = itemView.findViewById(R.id.meetingdate)
        val textContent: TextView = itemView.findViewById(R.id.textContent)
        val meetingtime : TextView = itemView.findViewById(R.id.meetingtime)
        val linearLayout:LinearLayout=itemView.findViewById(R.id.linearLayout)
        val deleteMeeting : ImageView = itemView.findViewById(R.id.deleteMeeting)
        val cardViewMeeting : CardView = itemView.findViewById(R.id.cardViewMeeting)
    }
}
interface IMeetingRVAdapter{
    fun onItemClicked(meeting: Meeting)
    fun getIntoActivity(meeting: Meeting)
}

