package com.example.revcurrency.data

data class CurrencyRateItem(
    val currency: String,
    val name: String = "",
    var rate: Float,
    var amount: Float
)