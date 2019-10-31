package com.example.revcurrency.data

data class CurrencyRateItem(
    val abbr: String,
    val name: String = "",
    var rate: Float,
    var amount: Float
)