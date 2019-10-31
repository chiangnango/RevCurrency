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
import java.util.*

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
        val currencyAmount = data[position]

        with(holder) {
            val context = itemView.context
            val drawableResId = context.resources.getIdentifier(
                "ic_${currencyAmount.first.toLowerCase(Locale.US).substring(0 until 2)}",
                "drawable",
                context.packageName
            )
            ImageUtil.loadCircleImage(drawableResId, icon)
            abbr.text = currencyAmount.first
            name.text = itemView.context.getString(R.string.currency_name, currencyAmount.first)
            amount.setText(currencyAmount.second.toString())
        }
    }

    override fun getItemCount(): Int = data.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.currency_icon
        val abbr: TextView = itemView.currency_abbr
        val name: TextView = itemView.currency_name
        val amount: EditText = itemView.amount
    }
}