package com.github.maximpestryakov.revolut.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity
data class LastUpdate(
    val date: Date,

    @PrimaryKey
    val primaryKey: Long = 1
)
