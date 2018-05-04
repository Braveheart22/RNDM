package com.johnstrack.rndm.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.johnstrack.rndm.R
import com.johnstrack.rndm.Utilities.DOCUMENT_KEY

class CommentActivity : AppCompatActivity() {
    lateinit var thoughtDocumentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_comment)
        thoughtDocumentId = intent.getStringExtra(DOCUMENT_KEY)
    }

    fun addCommentClicked (view: View) {

    }
}
