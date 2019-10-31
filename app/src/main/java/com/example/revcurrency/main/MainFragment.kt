package com.example.revcurrency.main

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.VERTICAL
import com.example.revcurrency.R
import com.example.revcurrency.architecture.InjectorUtil
import com.example.revcurrency.widget.CurrencyRateAdapter
import kotlinx.android.synthetic.main.fragment_main.*

class MainFragment : Fragment() {

    private lateinit var adapter: CurrencyRateAdapter

    private lateinit var viewModel: MainViewModel

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

        viewModel.fetchLatestRates()
    }

    private fun initViewModel() {
        viewModel = ViewModelProviders.of(this, InjectorUtil.provideMainViewModelFactory())
            .get(MainViewModel::class.java)

        viewModel.currencyRateList.observe(viewLifecycleOwner, Observer {

            adapter.currencyRateList = it
            adapter.notifyDataSetChanged()
        })

        viewModel.showSpinner.observe(viewLifecycleOwner, Observer { visible ->
            spinner.visibility = if (visible) {
                View.VISIBLE
            } else {
                View.GONE
            }
        })
    }

    private fun initView() {
        currency_list.layoutManager = LinearLayoutManager(context).apply {
            orientation = VERTICAL
        }

        adapter = CurrencyRateAdapter()
        currency_list.adapter = adapter
    }
}