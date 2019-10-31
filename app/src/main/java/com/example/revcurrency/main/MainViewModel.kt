package com.example.revcurrency.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.revcurrency.data.APIResult
import com.example.revcurrency.data.CurrencyRateItem
import com.example.revcurrency.data.LatestRates
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository) : ViewModel() {

    companion object {
        private val TAG = MainViewModel::class.java.simpleName

        private const val DEFAULT_AMOUNT = 100f
    }

    private var amount: Float = DEFAULT_AMOUNT

    private var currencyNameMap: Map<String, String>? = null

    private val _currencyRateList = MutableLiveData<MutableList<CurrencyRateItem>>()
    val currencyRateList: LiveData<MutableList<CurrencyRateItem>> = _currencyRateList

    private val _showSpinner = MutableLiveData<Boolean>()
    val showSpinner: LiveData<Boolean> = _showSpinner

    fun fetchLatestRates() {
        if (needFetchLatestRates()) {
            viewModelScope.launch {
                val rates = async { repository.fetchLatestRates() }
                val map = async { repository.fetchCurrencyNameMap() }
                handleFetchComplete(rates.await(), map.await())
            }

            _showSpinner.value = true
        }
    }

    private fun needFetchLatestRates(): Boolean {
        return _currencyRateList.value == null
    }

    private fun handleFetchSuccess(data: LatestRates) {
        fun getName(abbr: String): String = currencyNameMap?.get(abbr) ?: ""

        val currentList = _currencyRateList.value

        _currencyRateList.value = if (currentList == null) {
            mutableListOf<CurrencyRateItem>().apply {
                add(CurrencyRateItem(data.base, getName(data.base), 1.0f, amount))
                addAll(data.rates.entries.map {
                    CurrencyRateItem(it.key, getName(it.key), it.value, it.value * amount)
                })
            }
        } else {
            currentList.apply {
                forEach {
                    val abbr = it.abbr
                    if (abbr == data.base) {
                        it.rate = 1.0f
                        it.amount = amount
                    } else {
                        data.rates[abbr]?.let { newRate ->
                            if (it.rate != newRate) {
                                it.rate = newRate
                                it.amount = amount * newRate
                            }
                        }
                    }
                }
            }
        }
    }

    private fun handleFetchComplete(
        rateResult: APIResult<LatestRates>,
        mapResult: APIResult<Map<String, String>>
    ) {
        _showSpinner.value = false

        when (mapResult) {
            is APIResult.Success<Map<String, String>> -> currencyNameMap = mapResult.data
            else -> Unit // TODO: error handling
        }

        when (rateResult) {
            is APIResult.Success<LatestRates> -> handleFetchSuccess(rateResult.data)
            else -> Unit // TODO: error handling
        }
    }
}