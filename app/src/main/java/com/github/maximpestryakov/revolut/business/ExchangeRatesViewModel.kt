package com.github.maximpestryakov.revolut.business

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.toLiveData
import com.github.maximpestryakov.revolut.data.model.AllExchangeRates
import com.github.maximpestryakov.revolut.stripTrailingZerosFixed
import com.github.maximpestryakov.revolut.ui.ExchangeRateItem
import com.github.maximpestryakov.revolut.ui.ExchangeRatesState
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.BackpressureStrategy
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.Observables
import java.math.BigDecimal
import java.math.RoundingMode

class ExchangeRatesViewModel(
    private val exchangeRatesRepository: ExchangeRatesRepository,
    observeInternetConnectionChanges: ObserveInternetConnectionChanges,
    mainScheduler: Scheduler = AndroidSchedulers.mainThread()
) : ViewModel() {

    val state: LiveData<ExchangeRatesState>
    val internetConnectionChanges: LiveData<Boolean>

    private val changeBaseIntent: Relay<Pair<String, BigDecimal>> = PublishRelay.create()
    private val changeAmountIntent: Relay<BigDecimal> = PublishRelay.create()

    init {
        state = exchangeRatesRepository.getBaseCurrency()
            .map { it to DEFAULT_AMOUNT }
            .toObservable()
            .concatWith(changeBaseIntent)
            .switchMap { baseAndAmount ->
                val (newBase, newAmount) = baseAndAmount
                Observables.combineLatest(
                    exchangeRatesRepository.observeAllExchangeRates(newBase),
                    changeAmountIntent.startWith(newAmount)
                ) { allExchangeRates, amount -> mapToExchangeRatesState(amount, allExchangeRates) }
            }
            .toFlowable(BackpressureStrategy.LATEST)
            .replay(1)
            .autoConnect()
            .observeOn(mainScheduler)
            .toLiveData()

        internetConnectionChanges = observeInternetConnectionChanges().toFlowable(BackpressureStrategy.LATEST)
            .toLiveData()
    }

    fun changeBase(base: String, amount: BigDecimal) = changeBaseIntent.accept(base to amount)

    fun changeAmount(amount: BigDecimal) = changeAmountIntent.accept(amount)

    companion object {

        private val DEFAULT_AMOUNT = BigDecimal.ONE

        private fun mapToExchangeRatesState(amount: BigDecimal, allExchangeRates: AllExchangeRates): ExchangeRatesState {
            val items = allExchangeRates.exchangeRates
                .map {
                    ExchangeRateItem(
                        it.currency,
                        (it.rate * amount)
                            .setScale(2, RoundingMode.HALF_UP)
                            .stripTrailingZerosFixed(),
                        it.isBase
                    )
                }
            return ExchangeRatesState(allExchangeRates.lastUpdate, items)
        }
    }
}
