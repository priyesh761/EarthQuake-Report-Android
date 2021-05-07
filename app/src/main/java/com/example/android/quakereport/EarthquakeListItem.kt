package com.example.android.quakereport

data class EarthquakeListItem(
        val magnitude: Double,
        val source: String,
        val time: Long,
        val uRL: String
)