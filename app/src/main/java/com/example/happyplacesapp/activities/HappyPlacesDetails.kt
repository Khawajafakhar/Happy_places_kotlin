package com.example.happyplacesapp.activities

import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.widget.Toolbar
import com.example.happyplacesapp.R
import com.example.happyplacesapp.models.HappyPlace

class HappyPlacesDetails : AppCompatActivity() {
    private var tlHappyPlacesDetails : Toolbar? =null
    private var ivPlaceImage : ImageView? =null
    private var tvDescription: TextView? =null
    private var tvLocation: TextView? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_places_details)
         tlHappyPlacesDetails =findViewById(R.id.toolbar_happy_places_details)
        ivPlaceImage = findViewById(R.id.iv_place_image)
        tvDescription = findViewById(R.id.tv_description)
        tvLocation = findViewById(R.id.tv_location)
        setSupportActionBar(tlHappyPlacesDetails)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        tlHappyPlacesDetails?.setNavigationOnClickListener {
            onBackPressed()
        }
        var happyPlaceDetails : HappyPlace? =null
        if(intent.hasExtra(MainActivity.HAPPY_PLACE_OBJECT)){
            happyPlaceDetails = intent.getParcelableExtra(MainActivity.HAPPY_PLACE_OBJECT) as HappyPlace?
        }
        if (happyPlaceDetails != null){

            supportActionBar?.title = happyPlaceDetails.title
            ivPlaceImage?.setImageURI(Uri.parse(happyPlaceDetails.image))
            tvDescription?.text =happyPlaceDetails.description
            tvLocation?.text = happyPlaceDetails.location

        }

    }
}