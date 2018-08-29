package com.github.maximpestryakov.revolut.ui

import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.maximpestryakov.revolut.R
import com.github.maximpestryakov.revolut.business.ExchangeRatesViewModel
import com.github.maximpestryakov.revolut.getDrawable
import kotlinx.android.extensions.LayoutContainer
import kotlinx.android.synthetic.main.item_exchange_rate.*
import java.math.BigDecimal

class ExchangeRatesListAdapter(
    private val context: Context,
    private val exchangeRatesViewModel: ExchangeRatesViewModel
) : ListAdapter<ExchangeRateItem, ExchangeRatesListAdapter.ExchangeRatesViewHolder>(ExchangeRateItemDiffCallback) {

    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExchangeRatesViewHolder {
        return ExchangeRatesViewHolder(inflater.inflate(R.layout.item_exchange_rate, parent, false))
    }

    override fun onBindViewHolder(holder: ExchangeRatesViewHolder, position: Int) = Unit

    override fun onBindViewHolder(holder: ExchangeRatesViewHolder, position: Int, payloads: MutableList<Any>) {
        val oldItem = if (payloads.isNotEmpty()) payloads[0] as? ExchangeRateItem else null
        holder.bindView(getItem(position), oldItem)
    }

    inner class ExchangeRatesViewHolder(
        override val containerView: View
    ) : RecyclerView.ViewHolder(containerView), LayoutContainer {

        private var textWatcher: TextWatcher? = null

        init {
            itemView.setOnClickListener { etAmount.requestFocus() }
        }

        fun bindView(newItem: ExchangeRateItem, oldItem: ExchangeRateItem?) {
            val (currency, amount, isBase) = newItem

            if (currency != oldItem?.currency) {
                imageView.setImageDrawable(context.getDrawable(getIconName(currency)))
            }

            tvCurrency.text = currency

            etAmount.removeTextChangedListener(textWatcher)

            val amountString = if (amount == BigDecimal.ZERO) "" else amount.toPlainString()
            if (etAmount.text.toString() != amountString) {
                etAmount.setText(amountString)
                etAmount.setSelection(amountString.length)
            }

            if (isBase) {
                textWatcher = AmountChangedListener()
                etAmount.addTextChangedListener(textWatcher)
            } else {
                textWatcher = null
            }

            etAmount.setOnFocusChangeListener { _, hasFocus ->
                if (!isBase && hasFocus) {
                    exchangeRatesViewModel.changeBase(currency, amount)
                }
            }
        }

        private fun getIconName(currency: String) = "ic_${currency.toLowerCase()}"

        private inner class AmountChangedListener : TextWatcher {

            override fun afterTextChanged(s: Editable) {
                val amount = if (s.isEmpty()) BigDecimal.ZERO else s.toString().toBigDecimal()
                exchangeRatesViewModel.changeAmount(amount)
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
        }
    }
}
