package com.johnstrack.rndm

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private var selectedCategory = FUNNY
    private lateinit var thoughtsAdapter: ThoughtsAdapter
    private val thoughts = arrayListOf<Thought>()
    private val thoughtsCollectRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
    private lateinit var thoughtsListener: ListenerRegistration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val addThoughtIntent = Intent(this, AddThoughtActivity::class.java)
            startActivity(addThoughtIntent)
        }

        thoughtsAdapter = ThoughtsAdapter(thoughts)
        thoughtListView.adapter = thoughtsAdapter
        val layoutManager = LinearLayoutManager(this)
        thoughtListView.layoutManager = layoutManager
    }

    override fun onResume() {
        super.onResume()
        setListener()
    }

    private fun setListener() {

        if (selectedCategory == POPULAR) {
            thoughtsListener = thoughtsCollectRef
                    .orderBy(NUM_LIKES, Query.Direction.DESCENDING)
                    .addSnapshotListener(this) { snapshot, exception ->

                        if (exception != null) {
                            Log.e("Exception", "Could not retrieve documetns: ${exception.localizedMessage}")
                        }

                        if (snapshot != null) {

                            thoughts.clear()

                            for (document in snapshot?.documents!!) {
                                val data = document.data
                                val name = data!![USERNAME] as String
                                val timestamp = data[TIMESTAMP] as Date
                                val thoughtTxt = data[THOUGHT_TXT] as String
                                val numLikes = data[NUM_LIKES] as Long
                                val numComments = data[NUM_COMMENTS] as Long
                                val documentId = document.id

                                val newThought = Thought(name, timestamp, thoughtTxt, numLikes.toInt(), numComments.toInt(), documentId)
                                thoughts.add(newThought)
                            }
                            thoughtsAdapter.notifyDataSetChanged()
                        }
                    }
        } else {
            thoughtsListener = thoughtsCollectRef
                    .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                    .whereEqualTo(CATEGORY, selectedCategory)
                    .addSnapshotListener(this) { snapshot, exception ->

                        if (exception != null) {
                            Log.e("Exception", "Could not retrieve documetns: ${exception.localizedMessage}")
                        }

                        if (snapshot != null) {

                            thoughts.clear()

                            for (document in snapshot?.documents!!) {
                                val data = document.data
                                val name = data!![USERNAME] as String
                                val timestamp = data[TIMESTAMP] as Date
                                val thoughtTxt = data[THOUGHT_TXT] as String
                                val numLikes = data[NUM_LIKES] as Long
                                val numComments = data[NUM_COMMENTS] as Long
                                val documentId = document.id

                                val newThought = Thought(name, timestamp, thoughtTxt, numLikes.toInt(), numComments.toInt(), documentId)
                                thoughts.add(newThought)
                            }
                            thoughtsAdapter.notifyDataSetChanged()
                        }
                    }
        }
    }

    fun mainFunnyClicked(view: View) {
        mainFunnyBtn.isChecked = true
        mainSeriousBtn.isChecked = false
        mainCrazyBtn.isChecked = false
        mainPopularBtn.isChecked = false
        selectedCategory = FUNNY

        thoughtsListener.remove()
        setListener()
    }

    fun mainSeriousClicked(view: View) {
        mainFunnyBtn.isChecked = false
        mainSeriousBtn.isChecked = true
        mainCrazyBtn.isChecked = false
        mainPopularBtn.isChecked = false
        selectedCategory = SERIOUS

        thoughtsListener.remove()
        setListener()
    }

    fun mainCrazyClicked(view: View) {
        mainFunnyBtn.isChecked = false
        mainSeriousBtn.isChecked = false
        mainCrazyBtn.isChecked = true
        mainPopularBtn.isChecked = false
        selectedCategory = CRAZY

        thoughtsListener.remove()
        setListener()
    }

    fun mainPopularClicked(view: View) {
        mainFunnyBtn.isChecked = false
        mainSeriousBtn.isChecked = false
        mainCrazyBtn.isChecked = false
        mainPopularBtn.isChecked = true
        selectedCategory = POPULAR

        thoughtsListener.remove()
        setListener()
    }
}