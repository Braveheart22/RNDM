package com.johnstrack.rndm

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
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
        data["category"] = selectedCategory
        data["numComments"] = 0
        data["numLikes"] = 0
        data["thoughtTxt"] = addThoughtTxt.text.toString()
        data["timestamp"] = FieldValue.serverTimestamp()
        data["userName"] = addUsernameTxt.text.toString()

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