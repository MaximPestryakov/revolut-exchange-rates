package com.github.maximpestryakov.revolut.data.model

import com.squareup.moshi.JsonClass
import java.math.BigDecimal
import java.util.Date

@JsonClass(generateAdapter = true)
data class ExchangeRatesRemote(
    val base: String,
    val date: Date,
    val rates: Map<String, BigDecimal>
)
