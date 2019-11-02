package com.example.revcurrency.widget

import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT
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

    var onItemClickedListener: ((pos: Int) -> Unit)? = null

    var onTextChangedListener: ((afterText: String) -> Unit)? = null

    var currencyRateList: MutableList<CurrencyRateItem> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.widget_currency_rate_item, parent, false)).apply {

            fun itemClickListener() {
                val pos = adapterPosition
                if (pos == RecyclerView.NO_POSITION) {
                    return
                }

                onItemClickedListener?.invoke(pos)
            }

            itemView.setOnClickListener {
                itemClickListener()
            }

            amount.setOnFocusChangeListener focusChangeListener@{ v, hasFocus ->
                if (hasFocus) {
                    itemClickListener()
                } else {
                    hideIME(v.context, v as EditText)
                }
            }

            amount.addTextChangedListener(object : TextWatcher {
                override fun afterTextChanged(s: Editable?) {
                    if (!amount.editByUser()) {
                        return
                    }

                    if (adapterPosition == RecyclerView.NO_POSITION) {
                        return
                    }

                    onTextChangedListener?.invoke(s?.toString() ?: "")
                }

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) = Unit
            })
        }
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = currencyRateList[position]

        with(holder) {
            val context = itemView.context
            val drawableResId =
                context.resources.getIdentifier(getCurrencyIconName(data.currency), "drawable", context.packageName)
            ImageUtil.loadCircleImage(drawableResId, icon)
            currency.text = data.currency
            name.text = data.name
            amount.setTextFromProgram(data.amount.toString())
        }
    }

    override fun getItemCount(): Int = currencyRateList.size

    fun focusOnAmountShowIME(recyclerView: RecyclerView, pos: Int) {
        (recyclerView.findViewHolderForAdapterPosition(pos) as? ViewHolder)?.amount?.let { editText ->
            editText.requestFocus()
            showIME(recyclerView.context, editText)
            // keep cursor at the rightmost
            editText.setSelection(editText.text.length)
        }
    }

    private fun showIME(context: Context, editText: EditText) {
        (context.applicationContext.getSystemService(INPUT_METHOD_SERVICE) as? InputMethodManager)?.showSoftInput(
            editText, SHOW_IMPLICIT)
    }

    private fun hideIME(context: Context, editText: EditText) {
        (context.applicationContext.getSystemService(
            INPUT_METHOD_SERVICE) as? InputMethodManager)?.hideSoftInputFromWindow(editText.windowToken, 0)
    }


    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val icon: ImageView = itemView.currency_icon
        val currency: TextView = itemView.currency
        val name: TextView = itemView.currency_name
        val amount: EditText = itemView.amount
    }

    /**
     * Workaround for recognizing the EditText's text change is from program rather than user by set tag before setText() and remove it afterwards.
     */
    private fun EditText.setTextFromProgram(text: String) {
        tag = "Program"
        setText(text)
        tag = null
    }

    private fun EditText.editByUser(): Boolean {
        return tag == null
    }
}