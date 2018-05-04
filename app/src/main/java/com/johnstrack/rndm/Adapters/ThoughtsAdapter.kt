package com.johnstrack.rndm.Adapters

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.firestore.FirebaseFirestore
import com.johnstrack.rndm.Model.Thought
import com.johnstrack.rndm.R
import com.johnstrack.rndm.Utilities.NUM_LIKES
import com.johnstrack.rndm.Utilities.THOUGHTS_REF
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by John on 5/1/2018 at 3:33 PM.
 */
class ThoughtsAdapter(val thoughts: ArrayList<Thought>, val itemClick: (Thought) -> Unit): RecyclerView.Adapter<ThoughtsAdapter.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.thought_list_view, parent, false)
        return ViewHolder(view, itemClick   )
    }

    override fun getItemCount(): Int {
        return thoughts.count()
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindThought(thoughts[position])
    }

    inner class ViewHolder(itemView: View?, val itemClick: (Thought) -> Unit) : RecyclerView.ViewHolder(itemView) {

        private val userName = itemView?.findViewById<TextView>(R.id.listViewUsername)
        private val timestamp = itemView?.findViewById<TextView>(R.id.listViewTimestamp)
        private val thoughtTxt = itemView?.findViewById<TextView>(R.id.listViewThoughtTxt)
        private val numLikes = itemView?.findViewById<TextView>(R.id.listViewNumLikesLbl)
        private val likesImage = itemView?.findViewById<ImageView>(R.id.listViewLikesImage)

        fun bindThought (thought: Thought) {

            userName?.text = thought.userName
            thoughtTxt?.text = thought.thoughtTxt
            numLikes?.text = thought.numLikes.toString()

            val dateFormatter = SimpleDateFormat("MMM d, h:mm a", Locale.getDefault())
            val dateString = dateFormatter.format(thought.timestamp)
            timestamp?.text = dateString
            itemView.setOnClickListener { itemClick(thought) }

            likesImage?.setOnClickListener {
                FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thought.documentId)
                        .update(NUM_LIKES, thought.numLikes + 1)
            }
        }
    }
}