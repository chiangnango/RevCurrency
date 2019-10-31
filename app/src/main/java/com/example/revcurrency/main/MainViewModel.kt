package com.example.revcurrency.main

import androidx.lifecycle.*
import com.example.revcurrency.data.LatestRates
import com.example.revcurrency.util.MyLog
import kotlinx.coroutines.launch

class MainViewModel(private val repository: MainRepository) : ViewModel() {

    companion object {
        private val TAG = MainViewModel::class.java.simpleName

        private const val DEFAULT_AMOUNT = 100
    }

    private var amount = DEFAULT_AMOUNT

    val latestRates = repository.latestRates

    private val _currencyRateList = MutableLiveData<MutableList<Pair<String, Float>>>()
    val currencyRateList: LiveData<MutableList<Pair<String, Float>>> = _currencyRateList

    private val _showSpinner = MutableLiveData<Boolean>()
    val showSpinner: LiveData<Boolean> = _showSpinner

    private val latestRatesObserver = Observer<Result<LatestRates>> {
        MyLog.d(TAG, "LatestRates onChanged() $it")

        when {
            it.isSuccess -> {
                it.getOrNull()?.let { data ->
                    handleFetchSuccess(data)
                } ?: handleFetchFailure()
            }
            it.isFailure -> {
                handleFetchFailure()
            }
        }

        _showSpinner.value = false
    }

    init {
        repository.latestRates.observeForever(latestRatesObserver)
    }

    fun fetchLatestRates() {
        if (latestRates.value == null) {
            viewModelScope.launch {
                repository.fetchLatestRates()
            }
            _showSpinner.value = true
        }
    }

    private fun handleFetchSuccess(data: LatestRates) {
        val currentList = _currencyRateList.value
        _currencyRateList.value = if (currentList == null) {
            mutableListOf<Pair<String, Float>>().apply {
                add(Pair(data.base, amount.toFloat()))
                addAll(data.rates.entries.map {
                    Pair(it.key, it.value * amount)
                }.toMutableList())
            }
        } else {
            currentList.map {
                val name = it.first
                if (name == data.base) {
                    it
                } else {
                    data.rates[name]?.let { rate ->
                        Pair(name, rate * amount)
                    } ?: it
                }
            }.toMutableList()
        }
    }

    private fun handleFetchFailure() {
        // TODO: error handling
    }

    override fun onCleared() {
        super.onCleared()

        repository.latestRates.removeObserver(latestRatesObserver)
    }
}