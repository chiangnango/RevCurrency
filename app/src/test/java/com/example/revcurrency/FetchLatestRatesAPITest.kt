package com.example.revcurrency

import com.example.revcurrency.api.FetchLatestRatesAPI
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FetchLatestRatesAPITest {

    @Test
    fun `GIVEN latest rates json raw data WHEN parse to data THEN both data and order correct`() {
        val latestRates = FetchLatestRatesAPI().parseResult(LATEST_RATES_JSON)

        with(latestRates) {
            assertThat(base).isEqualTo("EUR")
            assertThat(date).isEqualTo("2018-09-06")
            assertThat(rates).hasSize(3)
            assertThat(rates["AUD"]).isEqualTo(1.611f)
            assertThat(rates["BRL"]).isEqualTo(4.7757f)
            assertThat(rates["BGN"]).isEqualTo(1.9492f)
            assertThat(rates.toList()[0].first).isEqualTo("AUD")
            assertThat(rates.toList()[1].first).isEqualTo("BRL")
            assertThat(rates.toList()[2].first).isEqualTo("BGN")
        }
    }

    companion object {
        private const val LATEST_RATES_JSON = "{" +
                "\"base\":\"EUR\"," +
                "\"date\":\"2018-09-06\"," +
                "\"rates\":{\"AUD\":1.611,\"BRL\":4.7757,\"BGN\":1.9492}}"
    }
}