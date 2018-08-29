package com.github.maximpestryakov.revolut.ui

import java.util.Date

data class ExchangeRatesState(
    val lastUpdate: Date,
    val items: List<ExchangeRateItem>,
    val hasInternetConnection: Boolean = false
)
