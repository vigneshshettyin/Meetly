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
import com.vs.meetly.adapters.TeamAdapter.*
private lateinit var itemview: View
class TeamAdapter (private val listener:itemclicked,val context:Context,val userList:ArrayList<Map<String,String>>):
    RecyclerView.Adapter<ViewHolder>() {
    class ViewHolder(itemview:View):RecyclerView.ViewHolder(itemview) {
        val name:TextView = itemview.findViewById(R.id.name)
        val image:ImageView = itemview.findViewById(R.id.dp)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        itemview = LayoutInflater.from(parent.context).inflate(R.layout.list_item,parent,false)
        return ViewHolder(itemview)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val temp = userList[position]
        if(temp.get("name")==null){
            holder.name.text=temp.get("username")
        }else{
            holder.name.text=temp.get("name").toString()
        }
        Glide.with(holder.itemView.context).load(temp.get("image")).circleCrop().into(holder.image)
        itemview.setOnClickListener{
            listener.onitemclicked(userList[position])
        }
    }

    override fun getItemCount(): Int {
        return userList.size
    }
}
interface itemclicked{
    fun onitemclicked(datamap: Map<String, String>)
}


