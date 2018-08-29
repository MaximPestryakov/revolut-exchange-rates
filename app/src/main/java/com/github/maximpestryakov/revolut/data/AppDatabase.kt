package com.github.maximpestryakov.revolut.data

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.github.maximpestryakov.revolut.data.model.ExchangeRate
import com.github.maximpestryakov.revolut.data.model.LastUpdate
import com.github.maximpestryakov.revolut.data.util.RoomTypeConverters

@TypeConverters(value = [RoomTypeConverters::class])
@Database(entities = [ExchangeRate::class, LastUpdate::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {

    abstract val exchangeRateDao: ExchangeRateDao

    companion object {
        const val FILENAME = "app_database.db"
    }
}
