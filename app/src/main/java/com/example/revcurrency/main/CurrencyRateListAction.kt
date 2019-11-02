package com.example.revcurrency.main

sealed class CurrencyRateListAction {
    data class FocusOnAmountShowIME(val pos: Int) : CurrencyRateListAction()
    data class ShiftItemToTop(val pos: Int) : CurrencyRateListAction()
}