package com.johnstrack.rndm

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import kotlinx.android.synthetic.main.activity_add_thought.*
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.content_main.*

class MainActivity : AppCompatActivity() {

    var selectedCategory = FUNNY

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(toolbar)

        fab.setOnClickListener { view ->
            val addThoughtIntent = Intent(this, AddThoughtActivity::class.java)
            startActivity(addThoughtIntent)
        }
    }

    fun mainFunnyClicked(view: View) {
        mainFunnyBtn.isChecked = true
        mainSeriousBtn.isChecked = false
        mainCrazyBtn.isChecked = false
        mainPopularBtn.isChecked = false
        selectedCategory = FUNNY
    }

    fun mainSeriousClicked(view: View) {
        addFunnyBtn.isChecked = false
        addSeriousBtn.isChecked = true
        addCrazyBtn.isChecked = false
        mainPopularBtn.isChecked = false
        selectedCategory = SERIOUS
    }

    fun mainCrazyClicked(view: View) {
        addFunnyBtn.isChecked = false
        addSeriousBtn.isChecked = false
        addCrazyBtn.isChecked = true
        mainPopularBtn.isChecked = false
        selectedCategory = CRAZY
    }

    fun mainPopularClicked(view: View) {
        addFunnyBtn.isChecked = false
        addSeriousBtn.isChecked = false
        addCrazyBtn.isChecked = false
        mainPopularBtn.isChecked = true
        selectedCategory = POPULAR
    }
}