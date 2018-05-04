package com.johnstrack.rndm.Activities

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.johnstrack.rndm.R
import com.johnstrack.rndm.Utilities.*
import kotlinx.android.synthetic.main.activity_add_thought.*

class AddThoughtActivity : AppCompatActivity() {

    private var selectedCategory = FUNNY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_thought)
    }

    fun addFunnyClicked(view: View) {
        addSeriousBtn.isChecked = false
        addCrazyBtn.isChecked = false
        addFunnyBtn.isChecked = true
        selectedCategory = FUNNY
    }

    fun addSeriousClicked(view: View) {
        addSeriousBtn.isChecked = true
        addCrazyBtn.isChecked = false
        addFunnyBtn.isChecked = false
        selectedCategory = SERIOUS

    }

    fun addCrazyClicked(view: View) {
        addSeriousBtn.isChecked = false
        addCrazyBtn.isChecked = true
        addFunnyBtn.isChecked = false
        selectedCategory = CRAZY
    }

    fun addPostClicked(view: View) {
        // Add post to Firestore!

        val data = HashMap<String, Any>()
        data[CATEGORY] = selectedCategory
        data[NUM_COMMENTS] = 0
        data[NUM_LIKES] = 0
        data[THOUGHT_TXT] = addThoughtTxt.text.toString()
        data[TIMESTAMP] = FieldValue.serverTimestamp()
        data[USERNAME] = FirebaseAuth.getInstance().currentUser?.displayName.toString()

        FirebaseFirestore.getInstance().collection(THOUGHTS_REF)
                .add(data)
                .addOnSuccessListener {
                    finish()
                }
                .addOnFailureListener { exception ->
                    Log.e("EXCEPTION: ", "Could not add post: $exception")
                }
    }
}