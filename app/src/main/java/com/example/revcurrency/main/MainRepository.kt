package com.example.revcurrency.main

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.revcurrency.api.FetchLatestRatesAPI
import com.example.revcurrency.data.LatestRates

class MainRepository {

    @VisibleForTesting
    internal val _latestRates = MutableLiveData<Result<LatestRates>>()
    val latestRates: LiveData<Result<LatestRates>> = _latestRates

    suspend fun fetchLatestRates(baseCurrency: String? = null) {
        try {
            val result = FetchLatestRatesAPI(baseCurrency).await()
            _latestRates.value = Result.success(result)
        } catch (e: Exception) {
            _latestRates.value = Result.failure(e)
        }
    }
}