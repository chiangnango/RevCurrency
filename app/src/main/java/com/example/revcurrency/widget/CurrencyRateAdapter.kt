package com.example.revcurrency.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.revcurrency.R
import com.example.revcurrency.util.ImageUtil
import kotlinx.android.synthetic.main.widget_currency_rate_item.view.*

class CurrencyRateAdapter : RecyclerView.Adapter<CurrencyRateAdapter.ViewHolder>() {

    var data: MutableList<Pair<String, Float>> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(
                R.layout.widget_currency_rate_item,
                parent,
                false
            )
        ).apply {

        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val currencyRate = data[position]

        with(holder) {
            ImageUtil.load(currencyIconMap[currencyRate.first], icon)
            abbr.text = currencyRate.first
            name.text = itemView.context.getString(R.string.currency_name, currencyRate.first)
            amount.setText(100.toString())
        }
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.currency_icon
        val abbr: TextView = itemView.currency_abbr
        val name: TextView = itemView.currency_name
        val amount: EditText = itemView.amount
    }

    companion object {
        private val currencyIconMap = mapOf(
            "USD" to R.drawable.ic_usd,
            "EUR" to R.drawable.ic_eur,
            "SEK" to R.drawable.ic_sek
        )
    }
}