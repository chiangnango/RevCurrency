package com.example.revcurrency.widget

import com.example.revcurrency.widget.CurrencyRateAdapter.Companion.getCurrencyIconName
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class CurrencyRateAdapterTest {

    @Test
    fun `getCurrencyIconDrawableName GIVEN "USD" THEN "ic_us"`() {
        val drawableName = getCurrencyIconName("USD")
        assertThat(drawableName).isEqualTo("ic_us")
    }
}