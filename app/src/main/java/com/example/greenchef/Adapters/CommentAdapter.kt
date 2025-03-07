package com.example.greenchef.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.greenchef.DataClass.Comment
import com.example.greenchef.R
import com.example.greenchef.ViewModels.UsersViewModel
import com.squareup.picasso.Picasso
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class CommentAdapter(private val comments: List<Comment>) :
    RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    class CommentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(com.example.greenchef.R.id.userName)
        val commentText: TextView = itemView.findViewById(com.example.greenchef.R.id.commentText)
        val commentTime: TextView = itemView.findViewById(com.example.greenchef.R.id.commentTime)
        val userImageView: ImageView = itemView.findViewById(com.example.greenchef.R.id.userImageView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val commentView = LayoutInflater.from(parent.context)
            .inflate(com.example.greenchef.R.layout.row_comments, parent, false)
        return CommentViewHolder(commentView)
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        val comment = comments[position]
        // Bind data to views
        holder.commentTime.text = formatDate(comment.timestamp)
        holder.commentText.text = comment.comment

        UsersViewModel().getUserById(comment.ownerId) { user ->
            holder.userName.text = user.name
            if (user.photoUrl.isNotEmpty() && user.photoUrl.isNotBlank() && user.photoUrl != "null") {
                Picasso.get()
                    .load(user.photoUrl)
                    .placeholder(R.drawable.progress_animation)
                    .into(holder.userImageView)
            } else {
                holder.userImageView.setImageResource(R.drawable.baseline_person_24)
            }
        }
    }

    private fun formatDate(milliseconds: Long): String {
        val format = SimpleDateFormat("dd.MM.yy", Locale.getDefault())
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = milliseconds
        return format.format(calendar.time)
    }
}