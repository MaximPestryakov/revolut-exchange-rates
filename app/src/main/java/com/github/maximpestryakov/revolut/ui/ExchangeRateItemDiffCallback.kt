package com.github.maximpestryakov.revolut.ui

import androidx.recyclerview.widget.DiffUtil

object ExchangeRateItemDiffCallback : DiffUtil.ItemCallback<ExchangeRateItem>() {

    override fun areItemsTheSame(oldItem: ExchangeRateItem, newItem: ExchangeRateItem): Boolean {
        return oldItem.currency == newItem.currency
    }

    override fun areContentsTheSame(oldItem: ExchangeRateItem, newItem: ExchangeRateItem): Boolean {
        return oldItem == newItem
    }

    override fun getChangePayload(oldItem: ExchangeRateItem, newItem: ExchangeRateItem) = oldItem
}
