package com.example.nammanala

data class ReportModel(
    val title: String,
    val description: String,
    val latitude: String,
    val longitude: String,
    val timestamp: String,
    val status: String = "Pending",
    val timeAgo: String = "Just now",
    val village: String = "Namma Section"
)
