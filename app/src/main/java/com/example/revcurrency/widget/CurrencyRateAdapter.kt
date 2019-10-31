package com.example.revcurrency.widget

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.annotation.VisibleForTesting
import androidx.recyclerview.widget.RecyclerView
import com.example.revcurrency.R
import com.example.revcurrency.data.CurrencyRateItem
import com.example.revcurrency.util.ImageUtil
import kotlinx.android.synthetic.main.widget_currency_rate_item.view.*
import java.util.*

class CurrencyRateAdapter : RecyclerView.Adapter<CurrencyRateAdapter.ViewHolder>() {

    companion object {
        @VisibleForTesting
        internal fun getCurrencyIconName(currencyAbbr: String): String {
            return "ic_${currencyAbbr.toLowerCase(Locale.US).substring(0 until 2)}"
        }
    }

    var currencyRateList: MutableList<CurrencyRateItem> = mutableListOf()

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
        val data = currencyRateList[position]

        with(holder) {
            val context = itemView.context
            val drawableResId = context.resources.getIdentifier(
                getCurrencyIconName(data.abbr),
                "drawable",
                context.packageName
            )
            ImageUtil.loadCircleImage(drawableResId, icon)
            abbr.text = data.abbr
            name.text = data.name
            amount.setText(data.amount.toString())
        }
    }

    override fun getItemCount(): Int = currencyRateList.size

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.currency_icon
        val abbr: TextView = itemView.currency_abbr
        val name: TextView = itemView.currency_name
        val amount: EditText = itemView.amount
    }
}