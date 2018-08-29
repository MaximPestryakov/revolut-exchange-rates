package com.github.maximpestryakov.revolut.data

import com.github.maximpestryakov.revolut.data.model.ExchangeRatesRemote
import io.reactivex.Single
import retrofit2.http.GET
import retrofit2.http.Query

interface FetchExchangeRates {

    @GET("latest")
    operator fun invoke(@Query("base") base: String): Single<ExchangeRatesRemote>

    companion object {
        const val ENDPOINT = "https://revolut.duckdns.org/"
    }
}
