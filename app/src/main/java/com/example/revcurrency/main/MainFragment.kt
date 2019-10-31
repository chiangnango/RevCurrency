package com.example.revcurrency.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.example.revcurrency.R
import com.example.revcurrency.widget.CurrencyRateAdapter
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private lateinit var adapter: CurrencyRateAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_main, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        initViewModel()

        initView()
    }

    private fun initViewModel() {

    }

    private fun initView() {
        currency_list.layoutManager = LinearLayoutManager(context).apply {
            orientation = VERTICAL
        }

        adapter = CurrencyRateAdapter().apply {
            data.add(Pair("USD", 1f))
            data.add(Pair("EUR", 1.2f))
            data.add(Pair("SEK", 0.33f))
        }

        currency_list.adapter = adapter
    }
}