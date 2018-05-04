package com.johnstrack.rndm.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.johnstrack.rndm.Adapters.CommentsAdapter
import com.johnstrack.rndm.Model.Comment
import com.johnstrack.rndm.R
import com.johnstrack.rndm.Utilities.*
import kotlinx.android.synthetic.main.activity_comment.*

class CommentActivity : AppCompatActivity() {
    lateinit var thoughtDocumentId: String
    lateinit var commentsAdapter: CommentsAdapter
    val comments = arrayListOf<Comment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        thoughtDocumentId = intent.getStringExtra(DOCUMENT_KEY)

        commentsAdapter = CommentsAdapter(comments)
        commentListView.adapter = commentsAdapter
        val layoutManager = LinearLayoutManager(this)
        commentListView.layoutManager = layoutManager
    }

    fun addCommentClicked (view: View) {

        val commentTxt = enterCommentTxt.text.toString()
        val thoughtRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF).document(thoughtDocumentId)

        FirebaseFirestore.getInstance().runTransaction {transaction ->

            val thought = transaction.get(thoughtRef)
            val numComments = thought.getLong(NUM_COMMENTS)!! + 1
            transaction.update(thoughtRef, NUM_COMMENTS, numComments)

            val newCommentRef = FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
                    .document(thoughtDocumentId).collection(COMMENTS_REF).document()

            val data = HashMap<String, Any>()
            data.put(COMMENT_TXT, commentTxt)
            data.put(TIMESTAMP, FieldValue.serverTimestamp())
            data.put(USERNAME, FirebaseAuth.getInstance().currentUser?.displayName.toString())

            transaction.set(newCommentRef, data)
        }
                .addOnSuccessListener {
                    enterCommentTxt.setText("")
                }
                .addOnFailureListener {exception ->
                    Log.e("Exception", "Could not add comment: ${exception.localizedMessage}")
                }
    }
}
