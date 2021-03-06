package com.johnstrack.rndm.Activities

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.johnstrack.rndm.Adapters.ThoughtsAdapter
import com.johnstrack.rndm.Interfaces.ThoughtOptionsClickListener
import com.johnstrack.rndm.Model.Thought
import com.johnstrack.rndm.R
import com.johnstrack.rndm.Utilities.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*
import java.util.*
import kotlin.concurrent.thread

class MainActivity : AppCompatActivity(), ThoughtOptionsClickListener {

    private var selectedCategory = FUNNY
    private lateinit var thoughtsAdapter: ThoughtsAdapter
    private val thoughts = arrayListOf<Thought>()
    private val thoughtsCollectRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
    private lateinit var thoughtsListener: ListenerRegistration
    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val addThoughtIntent = Intent(this, AddThoughtActivity::class.java)
            startActivity(addThoughtIntent)
        }

        thoughtsAdapter = ThoughtsAdapter(thoughts, this) {thought ->
            val commentActivity = Intent (this, CommentActivity::class.java)
            commentActivity.putExtra(DOCUMENT_KEY, thought.documentId)
            startActivity(commentActivity)

        }
        thoughtListView.adapter = thoughtsAdapter
        val layoutManager = LinearLayoutManager(this)
        thoughtListView.layoutManager = layoutManager
        auth = FirebaseAuth.getInstance()
    }

    override fun onResume() {
        super.onResume()
        updateUI()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onPrepareOptionsMenu(menu: Menu): Boolean {
        val menuItem = menu.getItem(0)
        if (auth.currentUser == null) {
            menuItem.title = "Login"
        } else {
            menuItem.title = "Logout"
        }
        return super.onPrepareOptionsMenu(menu)
    }

    override fun thoughtOptionsMenuClicked(thought: Thought) {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.options_menu, null)
        val deleteBtn = dialogView.findViewById<Button>(R.id.optionDeleteBtn)
        val editBtn = dialogView.findViewById<Button>(R.id.optionEditBtn)

        builder.setView(dialogView).setNegativeButton("Cancel") { _, _ -> }
        val ad = builder.show()

        deleteBtn.setOnClickListener {
            val thoughtRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thought.documentId)
            val collectionRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thought.documentId).collection(COMMENTS_REF)
            deleteCollection(collectionRef, thought) {success ->
                if (success) {
                    thoughtRef.delete()
                            .addOnSuccessListener {
                                ad.dismiss()
                            }
                            .addOnFailureListener {exception ->
                                Log.e("Exception", "Could not delete thought: ${exception.localizedMessage}")
                            }
                }
            }
        }

        editBtn.setOnClickListener {
//            val updateIntent = Intent(this, UpdateCommentActivity::class.java)
//            updateIntent.putExtra(THOUGHT_DOC_ID_EXTRA, thoughtDocumentId)
//            updateIntent.putExtra(COMMENT_DOC_ID_EXTRA, comment.documentId)
//            updateIntent.putExtra(COMMENT_TXT_EXTRA, comment.commentTxt)
//            ad.dismiss()
//            startActivity(updateIntent)
        }
    }

    fun deleteCollection (collection: CollectionReference, thought: Thought, complete: (Boolean) -> Unit) {
        collection.get().addOnSuccessListener { snapshot ->
            thread {
                val batch = FirebaseFirestore.getInstance().batch()
                for (document in snapshot) {
                    val docRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thought.documentId)
                            .collection(COMMENTS_REF).document(document.id)
                    batch.delete(docRef)
                }
                batch.commit()
                        .addOnSuccessListener {
                            complete(true)
                        }
                        .addOnFailureListener { exception ->
                            Log.e("Exception", "Could not delete subcollection: ${exception.localizedMessage}")
                        }
            }
        }
                .addOnFailureListener { exception ->
                    Log.e("Exception", "Could not do retrieve documents: ${exception.localizedMessage}")
                }
    }

    private fun updateUI () {
        if (auth.currentUser == null) {
            mainCrazyBtn.isEnabled = false
            mainFunnyBtn.isEnabled = false
            mainSeriousBtn.isEnabled = false
            mainPopularBtn.isEnabled = false
            fab.isEnabled = false
            thoughts.clear()
            thoughtsAdapter.notifyDataSetChanged()
        } else {
            mainCrazyBtn.isEnabled = true
            mainFunnyBtn.isEnabled = true
            mainSeriousBtn.isEnabled = true
            mainPopularBtn.isEnabled = true
            fab.isEnabled = true
            setListener()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.action_login) {
            if (auth.currentUser == null) {
                val loginIntent = Intent(this, LoginActivity::class.java)
                startActivity(loginIntent)
            } else {
                auth.signOut()
                updateUI()
            }
            return true
        }
        return false
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
                            parseData(snapshot)
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
                            parseData(snapshot)
                        }
                    }
        }
    }

    private fun parseData (snapshot: QuerySnapshot) {
        thoughts.clear()

        for (document in snapshot.documents) {
            val data = document.data
            val name = data!![USERNAME] as String
            val timestamp = data[TIMESTAMP] as Date
            val thoughtTxt = data[THOUGHT_TXT] as String
            val numLikes = data[NUM_LIKES] as Long
            val numComments = data[NUM_COMMENTS] as Long
            val documentId = document.id
            val userId = data[USER_ID] as String

            val newThought = Thought(name, timestamp, thoughtTxt, numLikes.toInt(), numComments.toInt(), documentId, userId)
            thoughts.add(newThought)
        }
        thoughtsAdapter.notifyDataSetChanged()
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