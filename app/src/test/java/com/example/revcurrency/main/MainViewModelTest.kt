package com.example.revcurrency.main

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.lifecycle.viewModelScope
import com.example.revcurrency.data.APIResult
import com.example.revcurrency.data.CurrencyRateItem
import com.example.revcurrency.data.LatestRates
import com.example.revcurrency.main.MainViewModel.Companion.DEFAULT_AMOUNT
import com.example.revcurrency.observeForTesting
import com.example.revcurrency.util.APIUtil
import com.google.common.truth.Truth.assertThat
import io.mockk.*
import io.mockk.impl.annotations.MockK
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.cancel
import kotlinx.coroutines.test.TestCoroutineDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@ExperimentalCoroutinesApi
class MainViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    private var testDispatcher = TestCoroutineDispatcher()

    private lateinit var viewModel: MainViewModel

    @MockK
    private lateinit var repository: MainRepository


    @Before
    fun setup() {
        // set Dispatchers.Main, this allows our test to run off-device
        Dispatchers.setMain(testDispatcher)

        MockKAnnotations.init(this, relaxed = true)
        viewModel = MainViewModel(repository)
    }

    @After
    fun teardown() {
        // reset main after the test is done
        Dispatchers.resetMain()
        // call this to ensure TestCoroutineDispater doesn't
        // accidentally carry state to the next test
        testDispatcher.cleanupTestCoroutines()
    }

    @Test
    fun `Verify fetch LatestRatesAPI per second, after fetch LatestRatesAPI, wait about 5 seconds, total fetch api 6 times`() =
        testDispatcher.runBlockingTest {
            coEvery { repository.fetchLatestRates(any()) } returns mockk(relaxed = true)
            coEvery { repository.fetchCurrencyNameMap() } returns mockk(relaxed = true)

            viewModel.fetchCurrencyRates()
            advanceTimeBy(5500)
            viewModel.viewModelScope.cancel()

            coVerify(exactly = 6) {
                repository.fetchLatestRates(APIUtil.DEFAULT_CURRENCY)
            }
            coVerify(exactly = 1) {
                repository.fetchCurrencyNameMap()
            }
        }

    @Test
    fun `Rotate device after fetch data complete, call fetchCurrencyRates() again won't fetch currencyNameMap and show loading again`() =
        testDispatcher.runBlockingTest {
            val fakeLatestRate = LatestRates("EUR", "2019-11-02", LinkedHashMap())
            val mockLatestRates = APIResult.Success(fakeLatestRate)
            val mockCurrencyNameMap = APIResult.Success<Map<String, String>>(emptyMap())
            coEvery { repository.fetchLatestRates(any()) } returns mockLatestRates
            coEvery { repository.fetchCurrencyNameMap() } returns mockCurrencyNameMap

            val showSpinnerObserver: Observer<Boolean> = mockk(relaxed = true)
            viewModel.showSpinner.observeForever(showSpinnerObserver)

            repeat(2) {
                viewModel.fetchCurrencyRates()
            }

            viewModel.viewModelScope.cancel()

            coVerify(exactly = 1) {
                repository.fetchCurrencyNameMap()
            }
            verify(exactly = 1) {
                showSpinnerObserver.onChanged(true)
            }
        }

    @Test
    fun `GIVEN different rates WHEN fetchComplete and update rates THEN rate and amount and order of list are correctly`() =
        testDispatcher.runBlockingTest {
            val firstRates = LatestRates(base = "EUR", date = "", rates = LinkedHashMap<String, Float>().apply {
                put("USD", 0.2f)
                put("NTD", 30f)
            })
            val secondRates = LatestRates(base = "EUR", date = "", rates = LinkedHashMap<String, Float>().apply {
                put("USD", 0.5f)
                put("NTD", 33f)
            })

            coEvery { repository.fetchLatestRates(any()) } returns APIResult.Success(firstRates)
            coEvery { repository.fetchCurrencyNameMap() } returns APIResult.Success(emptyMap())

            viewModel.currencyRateList.observeForTesting {
                viewModel.fetchCurrencyRates()

                assertThat(viewModel.currencyRateList.value).hasSize(3)
                assertThat(viewModel.currencyRateList.value!![0]).isEqualTo(
                    CurrencyRateItem("EUR", "", 1f, DEFAULT_AMOUNT))
                assertThat(viewModel.currencyRateList.value!![1]).isEqualTo(
                    CurrencyRateItem("USD", "", 0.2f, 0.2f * DEFAULT_AMOUNT))
                assertThat(viewModel.currencyRateList.value!![2]).isEqualTo(
                    CurrencyRateItem("NTD", "", 30f, 30f * DEFAULT_AMOUNT))

                // fetch LatestRates with new result
                coEvery { repository.fetchLatestRates(any()) } returns APIResult.Success(secondRates)
                advanceTimeBy(1000)

                assertThat(viewModel.currencyRateList.value).hasSize(3)
                assertThat(viewModel.currencyRateList.value!![0]).isEqualTo(
                    CurrencyRateItem("EUR", "", 1f, 1f * DEFAULT_AMOUNT))
                assertThat(viewModel.currencyRateList.value!![1]).isEqualTo(
                    CurrencyRateItem("USD", "", 0.5f, 0.5f * DEFAULT_AMOUNT))
                assertThat(viewModel.currencyRateList.value!![2]).isEqualTo(
                    CurrencyRateItem("NTD", "", 33f, 33f * DEFAULT_AMOUNT))
            }

            viewModel.viewModelScope.cancel()
        }

    @Test
    fun `GIVEN different rates with different base WHEN fetchComplete and update rates THEN rate and amount and order of list are correctly`() =
        testDispatcher.runBlockingTest {
            val firstRates = LatestRates(base = "EUR", date = "", rates = LinkedHashMap<String, Float>().apply {
                put("USD", 0.2f)
                put("NTD", 30f)
            })
            val ratesWithDifferentBase =
                LatestRates(base = "USD", date = "", rates = LinkedHashMap<String, Float>().apply {
                    put("NTD", 66f)
                    put("EUR", 2f)
                })

            coEvery { repository.fetchLatestRates(any()) } returns APIResult.Success(firstRates)
            coEvery { repository.fetchCurrencyNameMap() } returns APIResult.Success(emptyMap())

            viewModel.currencyRateList.observeForTesting {
                viewModel.fetchCurrencyRates()

                assertThat(viewModel.currencyRateList.value).hasSize(3)
                assertThat(viewModel.currencyRateList.value!![0]).isEqualTo(
                    CurrencyRateItem("EUR", "", 1f, DEFAULT_AMOUNT))
                assertThat(viewModel.currencyRateList.value!![1]).isEqualTo(
                    CurrencyRateItem("USD", "", 0.2f, 0.2f * DEFAULT_AMOUNT))
                assertThat(viewModel.currencyRateList.value!![2]).isEqualTo(
                    CurrencyRateItem("NTD", "", 30f, 30f * DEFAULT_AMOUNT))

                coEvery { repository.fetchLatestRates(any()) } returns APIResult.Success(ratesWithDifferentBase)
                advanceTimeBy(1000)
                viewModel.viewModelScope.cancel()

                assertThat(viewModel.currencyRateList.value).hasSize(3)
                assertThat(viewModel.currencyRateList.value!![0]).isEqualTo(
                    CurrencyRateItem("EUR", "", 1f, DEFAULT_AMOUNT))
                assertThat(viewModel.currencyRateList.value!![1]).isEqualTo(
                    CurrencyRateItem("USD", "", 0.5f, 0.5f * DEFAULT_AMOUNT))
                assertThat(viewModel.currencyRateList.value!![2]).isEqualTo(
                    CurrencyRateItem("NTD", "", 33f, 33f * DEFAULT_AMOUNT))
            }
        }

}