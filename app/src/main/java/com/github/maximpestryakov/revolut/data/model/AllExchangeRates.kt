package com.github.maximpestryakov.revolut.data.model

import java.util.Date

data class AllExchangeRates(
    val lastUpdate: Date,
    val exchangeRates: List<ExchangeRate>
)
