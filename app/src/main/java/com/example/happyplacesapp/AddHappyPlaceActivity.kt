package com.example.happyplacesapp

import android.app.DatePickerDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import androidx.appcompat.widget.Toolbar
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(),OnClickListener{
    private val cal = Calendar.getInstance()
   private var date :EditText? =null
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_happy_place)

        val toolbarAddPlace = findViewById<Toolbar>(R.id.toolbar_add_place)
        setSupportActionBar(toolbarAddPlace)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbarAddPlace.setNavigationOnClickListener {
            onBackPressed()
        }
         date = findViewById(R.id.et_date)

        dateSetListener = DatePickerDialog.OnDateSetListener {
                _, year, month, dayOfMoney ->
            cal.set(Calendar.YEAR,year)
            cal.set(Calendar.MONTH,month)
            cal.set(Calendar.DAY_OF_MONTH,dayOfMoney)
            updateDateInView()

        }
        date?.setOnClickListener(this)

        
    }

    override fun onClick(v: View?) {
        when (v!!.id){
            R.id.et_date ->{
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)).show()
            }
        }
    }
    private fun updateDateInView(){
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        date?.setText(sdf.format(cal.time).toString())
    }
}