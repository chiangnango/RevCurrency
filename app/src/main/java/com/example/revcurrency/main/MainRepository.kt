package com.example.revcurrency.main

import com.example.revcurrency.api.FetchCurrencyNameMapAPI
import com.example.revcurrency.api.FetchLatestRatesAPI
import com.example.revcurrency.data.APIResult
import com.example.revcurrency.data.LatestRates

class MainRepository {

    suspend fun fetchLatestRates(baseCurrency: String? = null): APIResult<LatestRates> {
        return try {
            val result = FetchLatestRatesAPI(baseCurrency).await()
            APIResult.Success(result)
        } catch (e: Exception) {
            APIResult.Failure(e)
        }
    }

    suspend fun fetchCurrencyNameMap(): APIResult<Map<String, String>> {
        return try {
            val result = FetchCurrencyNameMapAPI().await()
            APIResult.Success(result)
        } catch (e: Exception) {
            APIResult.Failure(e)
        }
    }
}