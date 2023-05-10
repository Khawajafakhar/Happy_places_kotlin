package com.example.happyplacesapp.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.Toolbar
import com.example.happyplacesapp.R

class HappyPlacesDetails : AppCompatActivity() {
    private var tlHappyPlacesDetails : Toolbar? =null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_places_details)
        setSupportActionBar(tlHappyPlacesDetails)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tlHappyPlacesDetails?.setNavigationOnClickListener {
            onBackPressed()
        }

    }
}