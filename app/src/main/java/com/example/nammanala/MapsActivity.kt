package com.example.nammanala

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolylineOptions

class MapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_maps)

        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment

        mapFragment.getMapAsync(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {

        mMap = googleMap

        // Default map location
        val canalLocation = LatLng(12.9716, 77.5946)

        // Load saved reports
        val sharedPreferences =
            getSharedPreferences("NammaNalaPrefs", MODE_PRIVATE)

        val reportsJson =
            sharedPreferences.getString("reports", "[]") ?: "[]"

        try {

            val jsonArray = org.json.JSONArray(reportsJson)

            for (i in 0 until jsonArray.length()) {

                val report =
                    jsonArray.getJSONObject(i)

                val latitude =
                    report.getString("latitude").toDouble()

                val longitude =
                    report.getString("longitude").toDouble()

                val title =
                    report.getString("title")

                val description =
                    report.getString("description")

                val reportLocation =
                    LatLng(latitude, longitude)

                mMap.addMarker(
                    MarkerOptions()
                        .position(reportLocation)
                        .title(title)
                        .snippet(description)
                )
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Canal path overlay
        val canalPath = listOf(
            LatLng(12.9716, 77.5946),
            LatLng(12.9730, 77.5980),
            LatLng(12.9750, 77.6020),
            LatLng(12.9780, 77.6050)
        )

        mMap.addPolyline(
            PolylineOptions()
                .addAll(canalPath)
                .width(8f)
        )

        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(12.9730, 77.5980))
                .title("Primary Canal")
                .snippet("Water Flow: Normal")
        )

        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(12.9750, 77.6020))
                .title("Secondary Canal")
                .snippet("Silt Accumulation Warning")
        )

        mMap.addMarker(
            MarkerOptions()
                .position(LatLng(12.9780, 77.6050))
                .title("Tail-End Zone")
                .snippet("Critical Low Water Flow")
        )

        // Zoom camera
        mMap.moveCamera(
            CameraUpdateFactory.newLatLngZoom(canalLocation, 15f)
        )
    }
}