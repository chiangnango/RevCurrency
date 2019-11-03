package com.example.revcurrency.api

import com.google.common.truth.Truth.assertThat
import org.junit.Test

class FetchCurrencyNameMapAPITest {

    @Test
    fun `GIVEN currency abbr & name json THEN correctly parse data`() {
        val result = FetchCurrencyNameMapAPI().parseResult(JSON)

        assertThat(result["EUR"]).isEqualTo("Euro")
        assertThat(result["USD"]).isEqualTo("United States Dollar")
        assertThat(result["JPY"]).isEqualTo("Japanese Yen")
    }

    companion object {
        private const val JSON =
            "{\"EUR\": \"Euro\",\"USD\": \"United States Dollar\",\"JPY\": \"Japanese Yen\"}"
    }
}