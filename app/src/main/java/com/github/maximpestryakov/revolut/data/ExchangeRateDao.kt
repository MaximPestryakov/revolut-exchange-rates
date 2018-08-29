package com.github.maximpestryakov.revolut.data

import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.github.maximpestryakov.revolut.data.model.AllExchangeRates
import com.github.maximpestryakov.revolut.data.model.ExchangeRate
import com.github.maximpestryakov.revolut.data.model.LastUpdate
import io.reactivex.Maybe
import io.reactivex.rxkotlin.Maybes
import java.util.Date

@Dao
abstract class ExchangeRateDao {

    fun getAllExchangeRates(base: String): Maybe<AllExchangeRates> {
        return Maybes.zip(getLastUpdate(), getExchangeRates(base)) { lastUpdate, exchangeRates ->
            AllExchangeRates(lastUpdate, exchangeRates)
        }
    }

    @Transaction
    open fun saveAllExchangeRates(allExchangeRates: AllExchangeRates) {
        saveLastUpdate(LastUpdate(allExchangeRates.lastUpdate))
        deleteExchangeRates()
        insertExchangeRates(allExchangeRates.exchangeRates)
    }

    private fun getExchangeRates(base: String): Maybe<List<ExchangeRate>> {
        return getExchangeRates()
            .map { convertToBase(it, base) }
            .doOnSuccess { updateExchangeRates(it) }
    }

    @Query("SELECT currency FROM ExchangeRate WHERE isBase = 1")
    abstract fun getBaseCurrency(): Maybe<String>

    @Query("SELECT date FROM LastUpdate")
    protected abstract fun getLastUpdate(): Maybe<Date>

    @Query("SELECT * FROM ExchangeRate")
    protected abstract fun getExchangeRates(): Maybe<List<ExchangeRate>>

    @Insert(onConflict = REPLACE)
    protected abstract fun saveLastUpdate(lastUpdate: LastUpdate)

    @Query("DELETE FROM ExchangeRate")
    protected abstract fun deleteExchangeRates()

    @Insert
    protected abstract fun insertExchangeRates(exchangeRates: List<ExchangeRate>)

    @Update
    protected abstract fun updateExchangeRates(exchangeRates: List<ExchangeRate>)

    companion object {

        private fun convertToBase(exchangeRates: List<ExchangeRate>, base: String): List<ExchangeRate> {
            val newBaseExchangeRate = exchangeRates.find { it.currency == base }!!
            return exchangeRates.map {
                it.copy(
                    rate = it.rate / newBaseExchangeRate.rate,
                    isBase = it.currency == newBaseExchangeRate.currency
                )
            }
        }
    }
}
