package com.github.maximpestryakov.revolut.di

import android.content.Context
import androidx.room.Room
import com.github.maximpestryakov.revolut.business.ExchangeRatesRepository
import com.github.maximpestryakov.revolut.business.ExchangeRatesViewModel
import com.github.maximpestryakov.revolut.business.InternetConnectionBroadcastReceiver
import com.github.maximpestryakov.revolut.business.ObserveInternetConnectionChanges
import com.github.maximpestryakov.revolut.data.AppDatabase
import com.github.maximpestryakov.revolut.data.ExchangeRateDao
import com.github.maximpestryakov.revolut.data.FetchExchangeRates
import com.github.maximpestryakov.revolut.data.util.BigDecimalAdapter
import com.squareup.moshi.Moshi
import com.squareup.moshi.adapters.Rfc3339DateJsonAdapter
import io.reactivex.schedulers.Schedulers
import org.koin.android.architecture.ext.viewModel
import org.koin.dsl.module.Module
import org.koin.dsl.module.applicationContext
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import java.math.BigDecimal
import java.util.Date

@Suppress("MoveLambdaOutsideParentheses")
object AppModule : Module by applicationContext({

    bean {
        Moshi.Builder()
            .add(Date::class.java, Rfc3339DateJsonAdapter())
            .add(BigDecimal::class.java, BigDecimalAdapter())
            .build()
    }

    bean {
        val moshi: Moshi = get()

        Retrofit.Builder()
            .addCallAdapterFactory(RxJava2CallAdapterFactory.createWithScheduler(Schedulers.io()))
            .addConverterFactory(MoshiConverterFactory.create(moshi))
            .baseUrl(FetchExchangeRates.ENDPOINT)
            .build()
            .create(FetchExchangeRates::class.java)
    }

    bean {
        val context: Context = get()

        Room.databaseBuilder(context, AppDatabase::class.java, AppDatabase.FILENAME)
            .build()
            .exchangeRateDao
    }

    bean {
        val fetchExchangeRates: FetchExchangeRates = get()
        val exchangeRateDao: ExchangeRateDao = get()

        ExchangeRatesRepository(fetchExchangeRates, exchangeRateDao)
    }

    bean {
        val context: Context = get()

        InternetConnectionBroadcastReceiver(context) as ObserveInternetConnectionChanges
    }

    viewModel {
        val exchangeRatesRepository: ExchangeRatesRepository = get()
        val observeInternetConnectionChanges: ObserveInternetConnectionChanges = get()

        ExchangeRatesViewModel(exchangeRatesRepository, observeInternetConnectionChanges)
    }
})
