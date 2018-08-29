package com.github.maximpestryakov.revolut.ui

import java.math.BigDecimal

data class ExchangeRateItem(
    val currency: String,
    val amount: BigDecimal,
    val isBase: Boolean = false
)
