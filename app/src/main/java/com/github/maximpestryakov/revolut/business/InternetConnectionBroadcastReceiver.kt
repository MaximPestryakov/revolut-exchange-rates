package com.github.maximpestryakov.revolut.business

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.NetworkInfo
import androidx.core.content.getSystemService
import com.jakewharton.rxrelay2.PublishRelay
import com.jakewharton.rxrelay2.Relay
import io.reactivex.Observable

class InternetConnectionBroadcastReceiver(
    context: Context
) : BroadcastReceiver(), ObserveInternetConnectionChanges {

    private val internetConnectionChanges: Relay<Boolean> = PublishRelay.create()

    init {
        context.registerReceiver(this, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onReceive(context: Context, intent: Intent) {
        context.getSystemService<ConnectivityManager>()?.let {
            val activeNetwork: NetworkInfo? = it.activeNetworkInfo
            val isConnected: Boolean = activeNetwork?.isConnectedOrConnecting ?: false
            internetConnectionChanges.accept(isConnected)
        }
    }

    override fun invoke(): Observable<Boolean> = internetConnectionChanges.distinctUntilChanged()
}
