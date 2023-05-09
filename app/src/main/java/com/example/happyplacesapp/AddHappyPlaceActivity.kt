package com.example.happyplacesapp

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.appsearch.SetSchemaRequest.READ_EXTERNAL_STORAGE
import android.content.ActivityNotFoundException
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.Toolbar
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), OnClickListener {
    private val cal = Calendar.getInstance()
    private var date: EditText? = null
    private var btnAddImage: TextView? = null
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
        btnAddImage = findViewById(R.id.tv_add_image)

        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMoney ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMoney)
            updateDateInView()

        }
        date?.setOnClickListener(this)
        btnAddImage?.setOnClickListener(this)


    }

    override fun onClick(v: View?) {
        when (v!!.id) {
            R.id.et_date -> {
                DatePickerDialog(
                    this@AddHappyPlaceActivity,
                    dateSetListener,
                    cal.get(Calendar.YEAR),
                    cal.get(Calendar.MONTH),
                    cal.get(Calendar.DAY_OF_MONTH)
                ).show()
            }
            R.id.tv_add_image -> {
                var pictureDialog = AlertDialog.Builder(this)
                pictureDialog.setTitle("Add Image")
                val pictureDialogueItem = arrayOf("Gallery", "Camera")
                pictureDialog.setItems(pictureDialogueItem) { _, which ->
                    when (which) {
                        0 -> getUserPermission()
                        1 -> Toast.makeText(this, "Image from Camera", Toast.LENGTH_SHORT).show()
                    }

                }
                pictureDialog.show()

            }
        }

    }

    private fun getUserPermission() {
        Dexter.withContext(this).withPermissions(
            android.Manifest.permission.READ_EXTERNAL_STORAGE,
            android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    Toast.makeText(
                        this@AddHappyPlaceActivity,
                        "Permissions Granted",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: List<PermissionRequest>,
                token: PermissionToken
            ) {
                showRationalDialogueForPermission()
            }
        }).onSameThread().check()
    }

    private fun showRationalDialogueForPermission() {
        AlertDialog.Builder(this).setTitle("Missing Permissions")
            .setMessage("Go to settings to allow required permissions")
            .setPositiveButton("Go to Settings") { _, _ ->
                try {
                    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                    val uri = Uri.fromParts("package", packageName, null)
                    intent.data = uri
                    startActivity(intent)
                } catch (e: ActivityNotFoundException) {
                    e.printStackTrace()
                }
            }.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }.show()


    }

    private fun updateDateInView() {
        val myFormat = "dd.MM.yyyy"
        val sdf = SimpleDateFormat(myFormat, Locale.getDefault())
        date?.setText(sdf.format(cal.time).toString())
    }
}