package com.example.happyplacesapp.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.example.happyplacesapp.R
import com.example.happyplacesapp.activities.AddHappyPlaceActivity
import com.example.happyplacesapp.database.DatabaseHandler
import com.google.android.material.floatingactionbutton.FloatingActionButton

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        var fabAddHappyPlaceActivity:FloatingActionButton = findViewById(R.id.fabAddPlace)
        fabAddHappyPlaceActivity.setOnClickListener {
            val intent = Intent(this, AddHappyPlaceActivity::class.java)
            startActivity(intent)
        }
        getAllPlacesFromDatabase()
    }

    private fun getAllPlacesFromDatabase(){
        val db = DatabaseHandler(this)
       val allHappyPlaces = db.getAllHappyPlaces()
        Log.e("Size",(allHappyPlaces.size > 0).toString())
        if(allHappyPlaces.size > 0){
            for(i in allHappyPlaces){
                Log.e("Title",i.title)
                Log.e("Description",i.description)
            }
        }
    }
}