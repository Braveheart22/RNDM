package com.johnstrack.rndm.Activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.johnstrack.rndm.Adapters.CommentsAdapter
import com.johnstrack.rndm.Interfaces.CommentOptionsClickListener
import com.johnstrack.rndm.Model.Comment
import com.johnstrack.rndm.R
import com.johnstrack.rndm.Utilities.*
import kotlinx.android.synthetic.main.activity_comment.*
import kotlinx.android.synthetic.main.activity_update_comment.*
import java.util.*

class CommentActivity : AppCompatActivity(), CommentOptionsClickListener {

    private lateinit var thoughtDocumentId: String
    private lateinit var commentsAdapter: CommentsAdapter
    private val comments = arrayListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        thoughtDocumentId = intent.getStringExtra(DOCUMENT_KEY)

        commentsAdapter = CommentsAdapter(comments, this)
        commentListView.adapter = commentsAdapter
        val layoutManager = LinearLayoutManager(this)
        commentListView.layoutManager = layoutManager

        FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocumentId)
                .collection(COMMENTS_REF)
                .orderBy(TIMESTAMP, Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, exception ->

                    if (exception != null) {
                        Log.e("Exception", "Could not retrieve comments: ${exception.localizedMessage}")
                    }

                    if (snapshot != null) {
                        comments.clear()
                        for (document in snapshot.documents) {
                            val data = document.data
                            val name = data!![USERNAME] as String
                            val timestamp = data[TIMESTAMP] as Date
                            val commentTxt = data[COMMENT_TXT] as String
                            val documentId = document.id
                            val userId = data[USER_ID] as String

                            val newComment = Comment(name, timestamp, commentTxt, documentId, userId)
                            comments.add(newComment)
                        }
                        commentsAdapter.notifyDataSetChanged()
                    }
                }
    }

    override fun optionsMenuClicked(comment: Comment) {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.options_menu, null)
        val deleteBtn = dialogView.findViewById<Button>(R.id.optionDeleteBtn)
        val editBtn = dialogView.findViewById<Button>(R.id.optionEditBtn)

        builder.setView(dialogView)
                .setNegativeButton("Cancel") { _, _ -> }
        val ad = builder.show()

        deleteBtn.setOnClickListener {
            val commentRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocumentId)
                    .collection(COMMENTS_REF).document(comment.documentId)
            val thoughtRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocumentId)

            FirebaseFirestore.getInstance().runTransaction { transaction ->

                val thought = transaction.get(thoughtRef)
                val numComments = thought.getLong(NUM_COMMENTS)!! - 1
                transaction.update(thoughtRef, NUM_COMMENTS, numComments)

                transaction.delete(commentRef)
            }.addOnSuccessListener {
                ad.dismiss()
            }.addOnFailureListener {exception ->
                Log.e("Exception", "Could not delete comment: ${exception.localizedMessage}")
            }
        }

        editBtn.setOnClickListener {
            val updateIntent = Intent(this, UpdateCommentActivity::class.java)
            updateIntent.putExtra(THOUGHT_DOC_ID_EXTRA, thoughtDocumentId)
            updateIntent.putExtra(COMMENT_DOC_ID_EXTRA, comment.documentId)
            updateIntent.putExtra(COMMENT_TXT_EXTRA, comment.commentTxt)
            startActivity(updateIntent)
            //
        }
    }

    fun addCommentClicked(view: View) {

        val commentTxt = enterCommentTxt.text.toString()
        val thoughtRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocumentId)

        FirebaseFirestore.getInstance().runTransaction { transaction ->

            val thought = transaction.get(thoughtRef)
            val numComments = thought.getLong(NUM_COMMENTS)!! + 1
            transaction.update(thoughtRef, NUM_COMMENTS, numComments)

            val newCommentRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
                    .document(thoughtDocumentId).collection(COMMENTS_REF).document()

            val data = HashMap<String, Any>()
            data[COMMENT_TXT] = commentTxt
            data[TIMESTAMP] = FieldValue.serverTimestamp()
            data[USERNAME] = FirebaseAuth.getInstance().currentUser?.displayName.toString()
            data[USER_ID] = FirebaseAuth.getInstance().currentUser?.uid.toString()

            transaction.set(newCommentRef, data)
        }
                .addOnSuccessListener {
                    enterCommentTxt.setText("")
                    dismissKeyboard()
                }
                .addOnFailureListener { exception ->
                    Log.e("Exception", "Could not add comment: ${exception.localizedMessage}")
                }
    }

    private fun dismissKeyboard() {

        val inputManager = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (inputManager.isAcceptingText) {
            inputManager.hideSoftInputFromWindow(currentFocus.windowToken, 0)
        }
    }
}