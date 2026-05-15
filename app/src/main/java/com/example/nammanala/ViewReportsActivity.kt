package com.example.nammanala

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.nammanala.databinding.ActivityViewReportsBinding
import org.json.JSONArray
import com.google.firebase.firestore.FirebaseFirestore

class ViewReportsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityViewReportsBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        firestore = FirebaseFirestore.getInstance()

        binding = ActivityViewReportsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        loadLatestReport()

        val currentTime = SimpleDateFormat(
            "hh:mm a",
            Locale.getDefault()
        ).format(Date())

        binding.tvDynamicLocation.append(
            "\nUpdated: $currentTime"
        )
    }

    private fun loadLatestReport() {

        val sharedPreferences =
            getSharedPreferences("NammaNalaPrefs", MODE_PRIVATE)

        val reportsJson =
            sharedPreferences.getString("reports", "[]") ?: "[]"

        try {

            val jsonArray = JSONArray(reportsJson)

            if (jsonArray.length() > 0) {

                val latestReport =
                    jsonArray.getJSONObject(jsonArray.length() - 1)

                val title =
                    latestReport.getString("title")

                val description =
                    latestReport.getString("description")

                val latitude =
                    latestReport.getString("latitude")

                val longitude =
                    latestReport.getString("longitude")

                binding.tvDynamicTitle.text = title

                binding.tvDynamicDescription.text =
                    description

                binding.tvDynamicLocation.text =
                    "Lat: $latitude   Lon: $longitude"
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}