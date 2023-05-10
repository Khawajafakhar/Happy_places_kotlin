package com.example.happyplacesapp.activities

import android.Manifest.*
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.widget.*
import androidx.appcompat.widget.Toolbar
import com.example.happyplacesapp.R
import com.example.happyplacesapp.database.DatabaseHandler
import com.example.happyplacesapp.models.HappyPlace
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

class AddHappyPlaceActivity : AppCompatActivity(), OnClickListener {
    private val cal = Calendar.getInstance()
    private var date: EditText? = null
    private var btnAddImage: TextView? = null
    private var ivSelectedImage: ImageView? = null
    private var btnSave: Button? = null
    private var etTitle: EditText? = null
    private var etDescription: EditText? = null
    private var etLocation: EditText? = null
    private lateinit var dateSetListener: DatePickerDialog.OnDateSetListener
    private var saveImageToInternalStorage: Uri? = null
    private var mLatitude: Double = 0.0
    private var mLongitude: Double = 0.0
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
        ivSelectedImage = findViewById(R.id.iv_place_image)
        btnSave = findViewById(R.id.btn_save)
        etTitle = findViewById(R.id.et_title)
        etDescription = findViewById(R.id.et_description)
        etLocation = findViewById(R.id.et_location)
        dateSetListener = DatePickerDialog.OnDateSetListener { _, year, month, dayOfMoney ->
            cal.set(Calendar.YEAR, year)
            cal.set(Calendar.MONTH, month)
            cal.set(Calendar.DAY_OF_MONTH, dayOfMoney)
            updateDateInView()

        }
        updateDateInView()

        date?.setOnClickListener(this)
        btnAddImage?.setOnClickListener(this)
        btnSave?.setOnClickListener(this)


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
                        1 -> getPhotoFromCamera()
                    }

                }
                pictureDialog.show()

            }
            R.id.btn_save -> {
                when {
                    etTitle?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Enter Title", Toast.LENGTH_SHORT).show()
                    }
                    etDescription?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show()
                    }
                    etLocation?.text.isNullOrEmpty() -> {
                        Toast.makeText(this, "Enter location", Toast.LENGTH_SHORT).show()
                    }
                    saveImageToInternalStorage == null -> {
                        Toast.makeText(this, "Select Image", Toast.LENGTH_SHORT).show()
                    }
                    else -> {
                        val happyPlace = HappyPlace(
                            0,
                            etTitle?.text.toString(),
                            saveImageToInternalStorage.toString(),
                            etDescription?.text.toString(),
                            date?.text.toString(),
                            etLocation?.text.toString(),
                            mLatitude,
                            mLongitude
                        )

                        val db = DatabaseHandler(this)
                        var dbResult=db.addHappyPlace(happyPlace)

                        if(dbResult>0){
                            setResult(Activity.RESULT_OK)
                            finish()
                        }
                    }
                }
            }
        }

    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GALLERY) {
                if (data != null) {
                    val contentUri = data.data
                    try {
                        val selectedImageBitmap =
                            MediaStore.Images.Media.getBitmap(this.contentResolver, contentUri)
                        saveImageToInternalStorage = saveImageToInternalStorage(selectedImageBitmap)
                        ivSelectedImage?.setImageBitmap(selectedImageBitmap)

                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            } else if (requestCode == CAMERA) {


                val thumbnail = data!!.extras!!.get("data") as Bitmap?
                saveImageToInternalStorage = saveImageToInternalStorage(thumbnail!!)
                ivSelectedImage?.setImageBitmap(thumbnail)


            }
        }
    }

    private fun getPhotoFromCamera() {
        Dexter.withContext(this).withPermissions(
            permission.CAMERA,
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                    startActivityForResult(intent, CAMERA)
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

    private fun getUserPermission() {
        Dexter.withContext(this).withPermissions(
            READ_EXTERNAL_STORAGE,
        ).withListener(object : MultiplePermissionsListener {
            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                if (report!!.areAllPermissionsGranted()) {
                    val intent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    startActivityForResult(intent, GALLERY)
                }
            }

            override fun onPermissionRationaleShouldBeShown(
                permissions: List<PermissionRequest>,
                token: PermissionToken
            ) {
                showRationalDialogueForPermission()
            }
        }).check()
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

    private fun saveImageToInternalStorage(bitmap: Bitmap): Uri {
        val wrapper = ContextWrapper(applicationContext)
        var file = wrapper.getDir(IMAGE_DIRECTORY, Context.MODE_PRIVATE)
        file = File(file, "${UUID.randomUUID()}.jpg")
        try {
            val stream: OutputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream)
            stream.flush()
            stream.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return Uri.parse(file.absolutePath)

    }

    companion object {
        private const val GALLERY = 1
        private const val CAMERA = 2
        private const val IMAGE_DIRECTORY = "HappyPlacesImages"

    }
}