package com.johnstrack.rndm.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.johnstrack.rndm.Model.Comment
import com.johnstrack.rndm.R
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by John on 5/4/2018 at 4:04 PM.
 */
class CommentsAdapter(private val comments: ArrayList<Comment>) : RecyclerView.Adapter<CommentsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.comment_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return comments.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindComment(comments[position])
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        private val userName = itemView?.findViewById<TextView>(R.id.commentListUsername)
        private val timestamp = itemView?.findViewById<TextView>(R.id.commentListTimestamp)
        private val commentTxt = itemView?.findViewById<TextView>(R.id.commentListCommentTxt)
        private val optionsImage = itemView?.findViewById<ImageView>(R.id.commentOptionsImage)


        fun bindComment(comment: Comment) {

            optionsImage?.visibility = View.INVISIBLE
            userName?.text = comment.username
            commentTxt?.text = comment.commentTxt

            val dateFormatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
            val dateString = dateFormatter.format(comment.timestamp)
            timestamp?.text = dateString

            if (FirebaseAuth.getInstance().currentUser?.uid == comment.userId) {
                optionsImage?.visibility = View.VISIBLE
            }
        }
    }
}