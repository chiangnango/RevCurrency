package com.example.revcurrency.data

data class LatestRates(
    val base: String,
    val date: String,
    val rates: LinkedHashMap<String, Float>
)