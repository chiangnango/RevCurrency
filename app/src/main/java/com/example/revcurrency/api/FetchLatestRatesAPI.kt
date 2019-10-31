package com.example.revcurrency.api

import com.example.revcurrency.data.LatestRates
import com.example.revcurrency.util.API
import com.example.revcurrency.util.APIUtil.URL
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class FetchLatestRatesAPI(baseCurrency: String = EUR) : API<LatestRates>() {

    companion object {
        private const val EUR = "EUR"
    }

    init {
        url = "${URL}/latest?base=${baseCurrency}"
    }

    @Throws(JsonSyntaxException::class)
    override fun parseResult(response: String): LatestRates {
        return Gson().fromJson(response, LatestRates::class.java)
    }
}