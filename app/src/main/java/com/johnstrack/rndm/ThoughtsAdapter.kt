package com.johnstrack.rndm

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by John on 5/1/2018 at 3:33 PM.
 */
class ThoughtsAdapter(val thoughts: ArrayList<Thought>): RecyclerView.Adapter<ThoughtsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.thought_list_view, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return thoughts.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder?.bindThought(thoughts[position])
    }

    inner class ViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView) {

        val userName = itemView?.findViewById<TextView>(R.id.listViewUsername)
        val timestamp = itemView?.findViewById<TextView>(R.id.listViewTimestamp)
        val thoughtTxt = itemView?.findViewById<TextView>(R.id.listViewThoughtTxt)
        val numLikes = itemView?.findViewById<TextView>(R.id.listViewNumLikesLbl)
        val likesImage = itemView?.findViewById<ImageView>(R.id.listViewLikesImage)

        fun bindThought (thought: Thought) {

            userName?.text = thought.userName
            thoughtTxt?.text = thought.thoughtTxt
            numLikes?.text = thought.numLikes.toString()

            val dateFormatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
            val dateString = dateFormatter.format(thought.timestamp)
            timestamp?.text = dateString
        }
    }
}