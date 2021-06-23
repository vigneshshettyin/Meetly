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
import com.vs.meetly.daos.UserDao
import com.vs.meetly.modals.User
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class UsersListAdapter(val context: Context, private val usersList: MutableList<String>) :
    RecyclerView.Adapter<UsersListAdapter.UsersListViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UsersListViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.users_meeting_list_horizonal, parent, false)
        return UsersListViewHolder(view)
    }

    override fun onBindViewHolder(holder: UsersListViewHolder, position: Int) {
        GlobalScope.launch {
            val userUid = usersList[position]
            val userdao = UserDao()
            val user = userdao.getUserById(userUid).await().toObject(User::class.java)!!
            withContext(Dispatchers.Main) {
                holder.userName.text = user.displayName
                holder.userEmail.text = user.email
                Glide.with(holder.userAvatar.context).load(user.imageUrl).circleCrop().into(holder.userAvatar)
            }
        }
    }

    override fun getItemCount(): Int {
        return usersList.size
    }

    inner class UsersListViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.users_list_meeting_name)
        val userEmail: TextView = itemView.findViewById(R.id.users_list_meeting_email)
        val userAvatar : ImageView = itemView.findViewById(R.id.users_list_meeting_image)
    }
}
