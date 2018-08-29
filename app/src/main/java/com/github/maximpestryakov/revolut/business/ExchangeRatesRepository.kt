package com.github.maximpestryakov.revolut.business

import com.github.maximpestryakov.revolut.data.ExchangeRateDao
import com.github.maximpestryakov.revolut.data.FetchExchangeRates
import com.github.maximpestryakov.revolut.data.model.AllExchangeRates
import com.github.maximpestryakov.revolut.data.model.ExchangeRate
import com.github.maximpestryakov.revolut.data.model.ExchangeRatesRemote
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import java.math.BigDecimal
import java.util.concurrent.TimeUnit

class ExchangeRatesRepository(
    private val fetchExchangeRates: FetchExchangeRates,
    private val exchangeRateDao: ExchangeRateDao,
    private val ioScheduler: Scheduler = Schedulers.io()
) {

    fun observeAllExchangeRates(base: String): Observable<AllExchangeRates> {
        return exchangeRateDao.getAllExchangeRates(base)
            .subscribeOn(ioScheduler)
            .toObservable()
            .concatWith(observeCurrenciesFromRemote(base))
            .map { sortAllExchangeRates(it) }
    }

    fun getBaseCurrency(): Single<String> {
        return exchangeRateDao.getBaseCurrency()
            .toSingle(DEFAULT_CURRENCY)
            .subscribeOn(ioScheduler)
    }

    private fun observeCurrenciesFromRemote(base: String): Observable<AllExchangeRates> {
        return Flowable.interval(0, 1, TimeUnit.SECONDS)
            .onBackpressureDrop()
            .concatMapMaybe {
                fetchExchangeRates(base)
                    .toMaybe()
                    .onErrorComplete()
            }
            .map { exchangeRatesRemoteToAllExchangeRates(it) }
            .doOnNext { exchangeRateDao.saveAllExchangeRates(it) }
            .toObservable()
    }

    companion object {
        private const val DEFAULT_CURRENCY = "USD"

        private fun sortAllExchangeRates(allExchangeRates: AllExchangeRates): AllExchangeRates {
            return allExchangeRates.copy(
                exchangeRates = allExchangeRates.exchangeRates.sortedWith(compareBy({ !it.isBase }, { it.currency }))
            )
        }

        private fun exchangeRatesRemoteToAllExchangeRates(exchangeRatesRemote: ExchangeRatesRemote): AllExchangeRates {
            val iterator = exchangeRatesRemote.rates.iterator()
            val size = exchangeRatesRemote.rates.size
            val exchangeRates = List(size + 1) { index ->
                if (index == 0) {
                    ExchangeRate(exchangeRatesRemote.base, BigDecimal.ONE, true)
                } else {
                    val (currency, rate) = iterator.next()
                    ExchangeRate(currency, rate)
                }
            }
            return AllExchangeRates(exchangeRatesRemote.date, exchangeRates)
        }
    }
}
