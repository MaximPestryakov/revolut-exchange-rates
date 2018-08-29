package com.github.maximpestryakov.revolut.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity
data class ExchangeRate(
    @PrimaryKey
    val currency: String,
    val rate: BigDecimal,
    val isBase: Boolean = false
)
