package com.example.android.stockmonitor

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.android.stockmonitor.databinding.ItemHolidayJavaBinding
import java.util.*

class StockAdapter :
    RecyclerView.Adapter<StockAdapter.MyViewHolder>() {
    private var stockList: List<StockModel>?
    override fun getItemCount(): Int {
        return if (stockList != null) stockList!!.size else 0
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): MyViewHolder {
        val binding: ItemHolidayJavaBinding = DataBindingUtil.inflate(
            LayoutInflater.from(parent.context),
            R.layout.item_holiday_java, parent, false
        )
        return MyViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: MyViewHolder,
        position: Int
    ) {
        holder.binding.setModel(stockList!![position])
    }

    fun addStockList(currencyList: List<StockModel>?) {
        stockList = currencyList
    }

    class MyViewHolder(binding: ItemHolidayJavaBinding) :
        RecyclerView.ViewHolder(binding.getRoot()) {
        val binding: ItemHolidayJavaBinding

        init {
            this.binding = binding
        }
    }

    init {
        stockList = ArrayList<StockModel>()
    }
}