package com.example.revcurrency.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.revcurrency.data.APIResult
import com.example.revcurrency.data.CurrencyRateItem
import com.example.revcurrency.data.LatestRates
import com.example.revcurrency.util.APIUtil.DEFAULT_CURRENCY
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository) : ViewModel() {

    companion object {
        private val TAG = MainViewModel::class.java.simpleName

        private const val DEFAULT_AMOUNT = 100f
    }

    private var amount: Float = DEFAULT_AMOUNT

    private var baseCurrency: String = DEFAULT_CURRENCY

    /**
     * Only be set when baseCurrency changed. If receive latestRates lacks new baseCurrency,
     * use this to calculate new rates of each currency based on changed baseCurrency.
     */
    private var conversionRate: Float = 1f

    private var currencyNameMap: Map<String, String>? = null

    private val _currencyRateList = MutableLiveData<MutableList<CurrencyRateItem>>()
    val currencyRateList: LiveData<MutableList<CurrencyRateItem>> = _currencyRateList

    private val _currencyRateListAction = MutableLiveData<CurrencyRateListAction>()
    val currencyRateListAction: LiveData<CurrencyRateListAction> = _currencyRateListAction

    private val _showSpinner = MutableLiveData<Boolean>()
    val showSpinner: LiveData<Boolean> = _showSpinner

    fun fetchLatestRates() {
        if (needFetchLatestRates()) {
            viewModelScope.launch {
                val deferredRates = async { repository.fetchLatestRates(baseCurrency) }
                val deferredMap = if (needFetchCurrencyNameMap()) {
                    async { repository.fetchCurrencyNameMap() }
                } else {
                    null
                }
                handleFetchComplete(deferredRates.await(), deferredMap?.await())
            }
            repeatFetchLatestRate()

            _showSpinner.value = true
        }
    }

    private fun repeatFetchLatestRate() {
        viewModelScope.launch {
            while (true) {
                delay(1_000L)
                handleFetchComplete(repository.fetchLatestRates(baseCurrency))
            }
        }
    }

    private fun needFetchLatestRates(): Boolean {
        return _currencyRateList.value == null
    }

    private fun needFetchCurrencyNameMap(): Boolean {
        return currencyNameMap == null
    }

    private fun handleFetchSuccess(latest: LatestRates) {
        fun getName(abbr: String): String = currencyNameMap?.get(abbr) ?: ""

        val currentList = _currencyRateList.value

        _currencyRateList.value = if (currentList == null) {
            mutableListOf<CurrencyRateItem>().apply {
                add(CurrencyRateItem(latest.base, getName(latest.base), 1.0f, amount))
                addAll(latest.rates.entries.map {
                    CurrencyRateItem(it.key, getName(it.key), it.value, it.value * amount)
                })
            }
        } else {
            // TODO: LatestRates and currentList diff check

            currentList.apply {
                val conversion = if (latest.base == baseCurrency) {
                    1f
                } else {
                    latest.rates[baseCurrency] ?: conversionRate
                }

                forEach {
                    val newRate = if (it.currency == latest.base) {
                        1.0f / conversion
                    } else {
                        val rate = latest.rates[it.currency] ?: return@forEach
                        rate / conversion
                    }

                    it.rate = newRate
                    it.amount = amount * newRate
                }
            }
        }
    }

    private fun handleFetchComplete(
        rateResult: APIResult<LatestRates>,
        mapResult: APIResult<Map<String, String>>? = null
    ) {
        _showSpinner.value = false

        when (mapResult) {
            null -> Unit
            is APIResult.Success<Map<String, String>> -> currencyNameMap = mapResult.data
            else -> Unit // TODO: error handling
        }

        when (rateResult) {
            is APIResult.Success<LatestRates> -> handleFetchSuccess(rateResult.data)
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
            conversionRate = newBaseItem.rate
            forEach {
                it.rate /= conversionRate
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