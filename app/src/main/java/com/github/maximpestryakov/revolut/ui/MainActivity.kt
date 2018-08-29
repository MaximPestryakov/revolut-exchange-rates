package com.github.maximpestryakov.revolut.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.github.maximpestryakov.revolut.R
import com.github.maximpestryakov.revolut.business.ExchangeRatesViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.android.architecture.ext.viewModel
import java.text.SimpleDateFormat
import java.util.Date

class MainActivity : AppCompatActivity() {

    private val exchangeRatesViewModel: ExchangeRatesViewModel by viewModel()
    private val noInternetSnackbar: Snackbar by lazy {
        Snackbar.make(container, R.string.no_connection, Snackbar.LENGTH_LONG)
    }
    private val dateFormat = SimpleDateFormat.getDateInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val exchangeRatesListAdapter = ExchangeRatesListAdapter(this, exchangeRatesViewModel)
        recyclerView.adapter = exchangeRatesListAdapter

        exchangeRatesViewModel.state
            .observe({ lifecycle }) { state ->
                exchangeRatesListAdapter.submitList(state.items)
                updateTitle(state.lastUpdate)
            }

        exchangeRatesViewModel.internetConnectionChanges
            .observe({ lifecycle }) { hasInternetConnection ->
                if (!hasInternetConnection && !noInternetSnackbar.isShownOrQueued) {
                    noInternetSnackbar.show()
                } else if (noInternetSnackbar.isShownOrQueued) {
                    noInternetSnackbar.dismiss()
                }
            }
    }

    private fun updateTitle(lastUpdate: Date) {
        toolbar.title = getString(R.string.exchange_rates_title_with_date, dateFormat.format(lastUpdate))
    }
}
