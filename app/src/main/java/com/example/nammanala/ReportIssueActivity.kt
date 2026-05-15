package com.example.nammanala

import android.Manifest
import android.content.pm.PackageManager
import android.content.Intent
import android.graphics.Bitmap
import android.location.Location
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.nammanala.databinding.ActivityReportIssueBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.firestore.FirebaseFirestore
class ReportIssueActivity : AppCompatActivity() {

    private lateinit var binding: ActivityReportIssueBinding
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var firestore: FirebaseFirestore
    private val requestCameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted: Boolean ->
        if (isGranted) {
            captureImage()
        } else {
            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    private val requestLocationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            getLastLocation()
        } else {
            Toast.makeText(this, getString(R.string.permission_denied), Toast.LENGTH_SHORT).show()
        }
    }

    private val captureImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap?
            if (imageBitmap != null) {
                binding.ivCaptured.setImageBitmap(imageBitmap)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityReportIssueBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)
        firestore = FirebaseFirestore.getInstance()

        binding.btnCaptureImage.setOnClickListener {
            checkCameraPermission()
        }

        binding.btnGetLocation.setOnClickListener {
            checkLocationPermission()
        }

        binding.btnSubmit.setOnClickListener {

            val title = binding.etTitle.text.toString().trim()
            val description = binding.etDescription.text.toString().trim()

            val latText = binding.tvLatitude.text.toString()
                .replace(getString(R.string.latitude_label), "")
                .trim()

            val lonText = binding.tvLongitude.text.toString()
                .replace(getString(R.string.longitude_label), "")
                .trim()

            if (title.isEmpty()) {
                Toast.makeText(this, "Please enter a title", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (description.isEmpty()) {
                Toast.makeText(this, "Please enter a description", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (latText.isEmpty() || latText == "--") {
                Toast.makeText(this, "Please get location first", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            saveReportLocally(title, description, latText, lonText)
            val reportData = hashMapOf(
                "title" to title,
                "description" to description,
                "latitude" to latText,
                "longitude" to lonText,
                "status" to "Pending"
            )

            firestore.collection("reports")
                .add(reportData)

            Toast.makeText(
                this,
                "Report Submitted Successfully!",
                Toast.LENGTH_LONG
            ).show()

            // Clear fields after submit
            binding.etTitle.text.clear()
            binding.etDescription.text.clear()
            binding.tvVillageDistance.text =
                "Nearest Village: --"

            binding.tvLatitude.text =
                "${getString(R.string.latitude_label)} --"

            binding.tvLongitude.text =
                "${getString(R.string.longitude_label)} --"

            binding.ivCaptured.setImageDrawable(null)
        }
    }

    private fun saveReportLocally(
        title: String,
        description: String,
        lat: String,
        lon: String
    ) {

        val sharedPreferences =
            getSharedPreferences("NammaNalaPrefs", MODE_PRIVATE)

        val reportsJson =
            sharedPreferences.getString("reports", "[]") ?: "[]"

        try {

            val jsonArray = org.json.JSONArray(reportsJson)

            val newReport = org.json.JSONObject().apply {

                put("title", title)
                put("description", description)
                put("latitude", lat)
                put("longitude", lon)

                put(
                    "date",
                    java.text.SimpleDateFormat(
                        "yyyy-MM-dd HH:mm",
                        java.util.Locale.getDefault()
                    ).format(java.util.Date())
                )

                put("status", "Pending")
                put("timeAgo", "Just now")
                put("village", "Namma Section")
            }

            jsonArray.put(newReport)

            sharedPreferences.edit()
                .putString("reports", jsonArray.toString())
                .apply()

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun checkCameraPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            captureImage()

        } else {

            requestCameraPermissionLauncher.launch(
                Manifest.permission.CAMERA
            )
        }
    }

    private fun captureImage() {

        val takePictureIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)

        captureImageLauncher.launch(takePictureIntent)
    }

    private fun checkLocationPermission() {

        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            getLastLocation()

        } else {

            requestLocationPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    private fun getLastLocation() {

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->

                if (location != null) {

                    binding.tvLatitude.text =
                        "${getString(R.string.latitude_label)} ${location.latitude}"

                    binding.tvLongitude.text =
                        "${getString(R.string.longitude_label)} ${location.longitude}"

                    Toast.makeText(
                        this,
                        "Location Fetched Successfully",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    // Demo fallback coordinates
                    binding.tvLatitude.text =
                        "${getString(R.string.latitude_label)} 12.9716"

                    binding.tvLongitude.text =
                        "${getString(R.string.longitude_label)} 77.5946"

                    binding.tvVillageDistance.text =
                        "Nearest Village: Harohalli (2.3 km)"

                    Toast.makeText(
                        this,
                        "Demo location loaded successfully",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            .addOnFailureListener {

                Toast.makeText(
                    this,
                    "Demo location loaded successfully",
                    Toast.LENGTH_SHORT
                ).show()

                binding.tvLatitude.text =
                    "${getString(R.string.latitude_label)} 12.9716"

                binding.tvLongitude.text =
                    "${getString(R.string.longitude_label)} 77.5946"
            }
    }
}