package com.example.revcurrency.api

import com.example.revcurrency.data.LatestRates
import com.example.revcurrency.util.API
import com.example.revcurrency.util.APIUtil.DEFAULT_CURRENCY
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException

class FetchLatestRatesAPI(baseCurrency: String = DEFAULT_CURRENCY) : API<LatestRates>() {

    init {
        url = "https://revolut.duckdns.org/latest?base=$baseCurrency"
    }

    @Throws(JsonSyntaxException::class)
    override fun parseResult(response: String): LatestRates {
        return Gson().fromJson(response, LatestRates::class.java)
    }
}