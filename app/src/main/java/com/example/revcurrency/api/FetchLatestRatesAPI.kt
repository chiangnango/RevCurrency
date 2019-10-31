package com.example.revcurrency.api

import com.example.revcurrency.data.LatestRates
import com.example.revcurrency.util.API
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class FetchLatestRatesAPI(baseCurrency: String? = null) : API<LatestRates>() {

    companion object {
        private const val EUR = "EUR"
    }

    init {
        url = "https://revolut.duckdns.org/latest?base=${baseCurrency ?: EUR}"
    }

    @Throws(JsonSyntaxException::class)
    override fun parseResult(response: String): LatestRates {
        return Gson().fromJson(response, LatestRates::class.java)
    }
}