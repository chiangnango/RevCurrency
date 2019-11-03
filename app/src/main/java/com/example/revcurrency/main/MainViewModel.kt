package com.example.revcurrency.main

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.revcurrency.data.APIResult
import com.example.revcurrency.data.CurrencyRateItem
import com.example.revcurrency.data.LatestRates
import com.example.revcurrency.util.APIUtil.DEFAULT_CURRENCY
import com.example.revcurrency.util.SingleLiveEvent
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository) : ViewModel() {

    companion object {
        private val TAG = MainViewModel::class.java.simpleName

        @VisibleForTesting
        internal const val DEFAULT_AMOUNT = 100f
    }

    private var amount: Float = DEFAULT_AMOUNT

    private var baseCurrency: String = DEFAULT_CURRENCY

    var currencyNameMap: Map<String, String>? = null

    private val _currencyRateList = MutableLiveData<MutableList<CurrencyRateItem>>()
    val currencyRateList: LiveData<MutableList<CurrencyRateItem>> = _currencyRateList

    private val _currencyRateListAction = SingleLiveEvent<CurrencyRateListAction>()
    val currencyRateListAction: LiveData<CurrencyRateListAction> = _currencyRateListAction

    val _showSpinner = SingleLiveEvent<Boolean>()
    val showSpinner: LiveData<Boolean> = _showSpinner

    fun fetchCurrencyRates() {
        if (needFetchLatestRates()) {
            _showSpinner.value = true

            viewModelScope.launch {
                val deferredRates = async { fetchLatestRates() }
                val deferredMap = if (needFetchCurrencyNameMap()) {
                    async { repository.fetchCurrencyNameMap() }
                } else {
                    null
                }

                fetchCurrencyNameComplete(deferredMap?.await())
                fetchLatestRatesComplete(deferredRates.await())
            }
            repeatFetchLatestRate()
        }
    }

    private suspend fun fetchLatestRates(): APIResult<LatestRates> {
        return repository.fetchLatestRates(baseCurrency)
    }

    private fun repeatFetchLatestRate() {
        viewModelScope.launch {
            while (true) {
                println("before delay ${Thread.currentThread().name} ${System.currentTimeMillis()}")
                delay(1_000L)
                println("after delay ${Thread.currentThread().name} ${System.currentTimeMillis()}")
                fetchLatestRatesComplete(fetchLatestRates())
                println("fetch complete ${Thread.currentThread().name}")
            }
        }
    }

    private fun needFetchLatestRates(): Boolean {
        return _currencyRateList.value == null
    }

    private fun needFetchCurrencyNameMap(): Boolean {
        return currencyNameMap == null
    }

    private fun updateCurrencyRateList(latest: LatestRates) {
        fun getName(abbr: String): String = currencyNameMap?.get(abbr) ?: ""

        val currentList = _currencyRateList.value

        println("updateCurrencyRateList ${Thread.currentThread().name} $latest $currentList")

        _currencyRateList.value = if (currentList == null) {
            baseCurrency = latest.base
            mutableListOf<CurrencyRateItem>().apply {
                add(CurrencyRateItem(latest.base, getName(latest.base), 1.0f, amount))
                addAll(latest.rates.entries.map {
                    CurrencyRateItem(it.key, getName(it.key), it.value, it.value * amount)
                })
            }
        } else {
            // TODO: LatestRates and currentList diff check, additional items or removed items

            // calculate new rates based on baseCurrency
            val newRatesMap = if (baseCurrency == latest.base) {
                HashMap<String, Float>().apply {
                    put(latest.base, 1.0f)
                    putAll(latest.rates)
                }
            } else {
                val conversion = latest.rates[baseCurrency]
                    ?: return   // TODO: error handling if latest rates don't contain baseCurrency

                HashMap<String, Float>().apply {
                    put(latest.base, 1.0f / conversion)

                    latest.rates.entries.forEach {
                        put(it.key, it.value / conversion)
                    }
                }
            }

            // update currentList via newRatesMap
            currentList.apply {
                forEach {
                    it.rate = newRatesMap[it.currency] ?: return@forEach
                    it.amount = amount * it.rate
                }
            }
        }
    }

    private fun fetchCurrencyNameComplete(mapResult: APIResult<Map<String, String>>? = null) {
        when (mapResult) {
            null -> Unit
            is APIResult.Success<Map<String, String>> -> currencyNameMap = mapResult.data
            else -> Unit // TODO: error handling
        }
    }

    private fun fetchLatestRatesComplete(rateResult: APIResult<LatestRates>) {
        _showSpinner.value = false
        when (rateResult) {
            is APIResult.Success<LatestRates> -> updateCurrencyRateList(rateResult.data)
            else -> Unit // TODO: error handling
        }
    }

    fun onItemClicked(pos: Int) {
        if (pos == 0) {
            _currencyRateListAction.value = CurrencyRateListAction.FocusOnAmountShowIME(pos)
            return
        }

        val currentList = _currencyRateList.value ?: return

        currentList.apply {
            add(0, removeAt(pos))

            val newBaseItem = get(0)
            baseCurrency = newBaseItem.currency
            amount = newBaseItem.amount
            forEach {
                it.rate /= newBaseItem.rate
            }
        }
        _currencyRateListAction.value = CurrencyRateListAction.ShiftItemToTop(pos)
        _currencyRateListAction.value = CurrencyRateListAction.FocusOnAmountShowIME(0)
    }

    fun onTextChanged(newText: String) {
        val newAmount = newText.toFloatOrNull() ?: 0f
        if (amount == newAmount) {
            return
        }

        amount = newAmount
        _currencyRateList.value?.apply {
            forEach {
                it.amount = amount * it.rate
            }
            _currencyRateList.value = this
        }
    }
}