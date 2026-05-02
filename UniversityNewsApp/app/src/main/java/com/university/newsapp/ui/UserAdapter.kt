package com.university.newsapp.ui

import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.university.newsapp.R
import com.university.newsapp.model.User

class UserAdapter(private val onClick: (User) -> Unit) :
    ListAdapter<User, UserAdapter.UserViewHolder>(DIFF_CALLBACK) {

    inner class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val avatar: TextView = itemView.findViewById(R.id.userAvatar)
        val name: TextView = itemView.findViewById(R.id.userName)
        val username: TextView = itemView.findViewById(R.id.userUsername)
        val email: TextView = itemView.findViewById(R.id.userEmail)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_user, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val user = getItem(position)
        holder.avatar.text = getInitials(user.name)
        val drawable = GradientDrawable()
        drawable.shape = GradientDrawable.OVAL
        drawable.setColor(getAvatarColor(user.name))
        holder.avatar.background = drawable
        holder.name.text = user.name
        holder.username.text = "@${user.username}"
        holder.email.text = user.email
        holder.itemView.setOnClickListener { onClick(user) }
    }

    private fun getInitials(name: String): String {
        val parts = name.trim().split(" ")
        return if (parts.size >= 2) "${parts[0][0]}${parts[1][0]}".uppercase()
        else if (parts.isNotEmpty() && parts[0].isNotEmpty()) "${parts[0][0]}".uppercase()
        else "?"
    }

    private fun getAvatarColor(name: String): Int {
        val colors = intArrayOf(
            0xFFE91E63.toInt(),
            0xFF9C27B0.toInt(),
            0xFF3F51B5.toInt(),
            0xFF2196F3.toInt(),
            0xFF009688.toInt(),
            0xFF4CAF50.toInt(),
            0xFFFF5722.toInt(),
            0xFF795548.toInt()
        )
        return colors[name.length % colors.size]
    }

    companion object {
        private val DIFF_CALLBACK = object : DiffUtil.ItemCallback<User>() {
            override fun areItemsTheSame(oldItem: User, newItem: User) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: User, newItem: User) = oldItem == newItem
        }
    }
}
