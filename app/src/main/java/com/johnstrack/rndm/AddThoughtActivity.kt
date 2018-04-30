package com.johnstrack.rndm

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View

class AddThoughtActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_thought)
    }

    fun addFunnyClicked (view: View) {

    }

    fun addSeriousClicked (view: View) {

    }

    fun addCrazyClicked (view: View) {

    }

    fun addPostClicked (view: View) {
        // Add post to Firestore!
    }
}