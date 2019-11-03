package com.example.revcurrency.api

import com.example.revcurrency.util.API
import com.google.gson.Gson
import com.google.gson.JsonSyntaxException
import com.google.gson.reflect.TypeToken

class FetchCurrencyNameMapAPI : API<Map<String, String>>() {

    init {
        url = "https://openexchangerates.org/api/currencies.json"
    }

    @Throws(JsonSyntaxException::class)
    override fun parseResult(response: String): Map<String, String> {
        val type = object : TypeToken<Map<String, String>>() {}.type
        return Gson().fromJson(response, type)
    }
}